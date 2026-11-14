"""한국관광공사 TourAPI 4.0 KorService2 최소 연동.

- 사용자의 장소명/주소와 TourAPI 결과가 정확히 맞는 경우에만 개요를 사용한다.
- API 키/인증 URL을 로그에 남기지 않는다.
- 외부 요청 실패시 호출자가 일반 장소 안내로 돌아갈 수 있게 예외를 발생시킨다.
"""
from __future__ import annotations

from dataclasses import dataclass
from threading import Lock
from time import monotonic
from collections import OrderedDict
from html import unescape
import logging
import os
import re
from urllib.parse import unquote

import requests

log = logging.getLogger("moa.tourapi")
BASE_URL = "https://apis.data.go.kr/B551011/KorService2"


@dataclass(frozen=True)
class TourFacts:
    title: str
    address: str
    overview: str
    content_id: str


class TourApiError(RuntimeError):
    """일시적인 외부 서비스 실패. URL/키를 포함하지 않는다."""


def _key() -> str:
    # data.go.kr에서 발급된 인코딩/디코딩 키 중 어느 쪽이든 1회만 인코딩되도록 한다.
    return unquote(os.getenv("TOUR_API_KEY", "").strip())


def _fetch(endpoint: str, *, service_key: str, **values: object) -> list[dict]:
    params = {
        "serviceKey": service_key,
        "MobileOS": "ETC",
        "MobileApp": "MOA",
        "_type": "json",
        "numOfRows": 100,
        "pageNo": 1,
        **values,
    }
    try:
        response = requests.get(
            f"{BASE_URL}/{endpoint}",
            params=params,
            headers={"Accept": "application/json"},
            timeout=(3, 7),
        )
        response.raise_for_status()
        raw = response.json()
    except (requests.RequestException, ValueError) as error:
        raise TourApiError(f"{endpoint} 요청 또는 JSON 응답 실패") from error

    if not isinstance(raw, dict):
        raise TourApiError("TourAPI 응답 형식 오류")
    envelope = raw.get("response", raw)
    if not isinstance(envelope, dict):
        raise TourApiError("TourAPI response 형식 오류")
    header = envelope.get("header") or {}
    code = str(header.get("resultCode", "")) if isinstance(header, dict) else ""
    if code not in ("0000", "0", "00"):
        # API 키나 원본 URL을 로깅하지 않는다.
        raise TourApiError(f"TourAPI {endpoint} 응답 코드: {code or '없음'}")
    body = envelope.get("body") or {}
    items = body.get("items") or {} if isinstance(body, dict) else {}
    if not isinstance(items, dict):
        return []
    rows = items.get("item") or []
    if isinstance(rows, dict):
        rows = [rows]
    if not isinstance(rows, list):
        return []
    return [row for row in rows if isinstance(row, dict)]


def _normalize_title(value: str) -> str:
    # 괄호로 표기된 별칭이나 공백 차이는 허용, 다른 장소명은 매칭하지 않는다.
    value = re.sub(r"\([^)]*\)|\[[^]]*\]", "", value or "")
    return re.sub(r"[^가-힣A-Za-z0-9]", "", value).casefold()


def _city(address: str) -> str:
    head = (address or "").split()
    if not head:
        return ""
    prefix = head[0]
    aliases = {
        "서울": ("서울", "서울특별시"), "부산": ("부산", "부산광역시"),
        "대구": ("대구", "대구광역시"), "인천": ("인천", "인천광역시"),
        "광주": ("광주", "광주광역시"), "대전": ("대전", "대전광역시"),
        "울산": ("울산", "울산광역시"), "세종": ("세종", "세종특별자치시"),
        "경기": ("경기", "경기도"), "강원": ("강원", "강원특별자치도", "강원도"),
        "충북": ("충북", "충청북도"), "충남": ("충남", "충청남도"),
        "전북": ("전북", "전북특별자치도", "전라북도"), "전남": ("전남", "전라남도"),
        "경북": ("경북", "경상북도"), "경남": ("경남", "경상남도"),
        "제주": ("제주", "제주특별자치도"),
    }
    for canonical, variants in aliases.items():
        if prefix in variants:
            return canonical
    return ""


def _local_parts(address: str) -> set[str]:
    parts = (address or "").split()
    # Metropolitan cities are already handled by _city.
    return {p for p in parts if p.endswith(("시", "군", "구"))
            and not p.endswith(("광역시", "특별시", "특별자치시"))}


def _same_region(expected: str, actual: str) -> bool:
    if not expected:
        return True
    if not actual:
        return False
    want, got = _city(expected), _city(actual)
    if want and want != got:
        return False
    locals_wanted = _local_parts(expected)
    if locals_wanted and not locals_wanted.issubset(_local_parts(actual)):
        return False
    # Bare locality: 강릉 must match 강릉시, never 속초시.
    if not want and not locals_wanted:
        expected_compact = re.sub(r"\s+", "", expected)
        if expected_compact not in re.sub(r"\s+", "", actual):
            return False
    return True


def _place_core(title: str, address: str) -> str:
    normalized = _normalize_title(title)
    # Remove a known local city/county prefix only. Never strip arbitrary suffixes
    # such as 주차장, 지점, 별관 or conflate 해변 and 해수욕장 blindly.
    prefixes = sorted((p[:-1] for p in _local_parts(address)
                       if p.endswith(("시", "군"))), key=len, reverse=True)
    for prefix in prefixes:
        if normalized.startswith(prefix) and len(normalized) > len(prefix) + 1:
            return normalized[len(prefix):]
    return normalized


def _query_variants(name: str, region: str) -> list[str]:
    clean = re.sub(r"\([^)]*\)|\[[^]]*\]", "", name).strip()
    values = [name.strip(), clean, re.sub(r"\s+", "", clean), _place_core(clean, region)]
    return list(dict.fromkeys(v[:60] for v in values if v))[:3]


def _clean_overview(raw: str) -> str:
    value = unescape(str(raw or ""))
    value = re.sub(r"<[^>]*>", " ", value)
    value = re.sub(r"\s+", " ", value).strip()
    return value[:1100]


# Successful facts only: misses and service errors are never cached.
_CACHE = OrderedDict()
_CACHE_LOCK = Lock()
_CACHE_TTL = 3600


@dataclass(frozen=True)
class TourLookup:
    facts: TourFacts | None
    status: str


def lookup_tour_facts(place_name: str, region: str) -> TourLookup:
    key = _key()
    if not key:
        return TourLookup(None, "not_configured")
    if not _normalize_title(place_name):
        return TourLookup(None, "invalid_name")
    cache_key = (place_name, region)
    with _CACHE_LOCK:
        cached = _CACHE.get(cache_key)
        if cached and cached[0] > monotonic():
            _CACHE.move_to_end(cache_key)
            return TourLookup(cached[1], "matched_cached")
        _CACHE.pop(cache_key, None)

    found = {}
    for keyword in _query_variants(place_name, region):
        for row in _fetch("searchKeyword2", service_key=key, keyword=keyword):
            if row.get("contentid"):
                found[str(row["contentid"])] = row
    if not found:
        return TourLookup(None, "no_results")

    matching = []
    name_hits = 0
    for item in found.values():
        title, address = str(item.get("title", "")), str(item.get("addr1", ""))
        exact = _normalize_title(title) == _normalize_title(place_name)
        # Prefix aliases require a supplied region AND matching address.
        alias = bool(region) and _place_core(title, address) == _place_core(place_name, address)
        if not (exact or alias):
            continue
        name_hits += 1
        if _same_region(region, address):
            matching.append(item)
    if not matching:
        return TourLookup(None, "region_mismatch" if name_hits else "name_mismatch")
    if len(matching) != 1:
        return TourLookup(None, "ambiguous")

    item = matching[0]
    details = _fetch("detailCommon2", service_key=key, contentId=item["contentid"])
    if not details:
        return TourLookup(None, "no_detail")
    detail = details[0]
    if detail.get("contentid") and str(detail["contentid"]) != str(item["contentid"]):
        return TourLookup(None, "detail_id_mismatch")
    overview = _clean_overview(detail.get("overview", ""))
    if not overview:
        return TourLookup(None, "no_overview")
    facts = TourFacts(title=str(item.get("title", place_name)),
                      address=str(item.get("addr1", "")), overview=overview,
                      content_id=str(item["contentid"]))
    with _CACHE_LOCK:
        _CACHE[cache_key] = (monotonic() + _CACHE_TTL, facts)
        while len(_CACHE) > 256:
            _CACHE.popitem(last=False)
    return TourLookup(facts, "matched")


def find_tour_facts(place_name: str, region: str) -> TourFacts | None:
    """Compatibility wrapper for existing imports."""
    return lookup_tour_facts(place_name, region).facts
