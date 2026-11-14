# Road Story 추천 도슨트 0.3.0

## 목표
‘역사·음식이 없으면 설명 불가’ 대신 ‘왜 이 장소에 들를 만한가’를 설명합니다.
역사·음식·풍경을 우선 소재로 삼되 근거가 있는 문화·체험 등도 활용합니다.
세 소재를 모두 채우거나 역사를 무조건 먼저 말하는 방식이 아닙니다.
특정 장소에 대한 하드코딩 자료는 없습니다. verified_facts.py를 더 이상 가져오지 않습니다.

## 적용
1. 기존 서버 폴더를 백업합니다.
2. app.py, tourapi_client.py를 교체하고 evidence.py를 같은 폴더에 추가합니다.
3. 기존 .env는 유지합니다. .env.example은 참고용이며 실제 키를 덮어쓰지 마세요.
4. `python -m pip install -r requirements.txt`
5. 기존 실행 방식으로 서버를 재시작합니다. 예: `python -m uvicorn app:app --host 0.0.0.0 --port 8000`
6. `/health`에서 version=0.3.0, apiKeyConfigured=true, webSearchEnabled=true를 확인합니다.
7. 앱에 남은 이전 도슨트 응답 캐시를 지우거나 캐시 버전을 올립니다.

기존 endpoint `/api/docent/story`와 placeName, region, topic, userInterests,
factSnippets, sourceName 요청 및 storyText 응답을 유지합니다.
새 필드는 모두 추가 방식이지만 Android 응답 파서가 알 수 없는 필드를 허용하는지 확인하세요.
status에는 ready 외에 insufficient_evidence, temporarily_unavailable이 추가됩니다.
새 enum 값을 허용하지 않는 앱은 수정해야 합니다.

## 공통 동작
1. TourAPI에서 이름·지역이 맞는 장소 개요 조회. 실패 상태 보존.
2. 실제 웹 검색을 반드시 수행하여 해당 장소의 역사·음식·풍경 및 방문 특징 조사.
3. 장소 자료를 확보하지 못했고 region이 있으면 지역 자료를 별도로 검색.
4. 앱 제공 사실을 추가하고 방문 가치 중심으로 2~3문장 생성.
5. 사용한 근거 ID, 출처, 장소/지역 범위, 소재를 응답에 반환.

TourAPI 키가 없어도 웹 검색은 동작합니다. OPENAI_API_KEY와 해당 계정의 검색 지원 모델 접근이 필요합니다.
OPENAI_SEARCH_MODEL 기본값은 gpt-6-astra, 이야기 작성은 기존 gpt-6-luna입니다.
다른 모델로 바꿀 경우 Responses API와 web_search 지원을 직접 확인해야 합니다.
문서: https://developers.openai.com/api/docs/guides/tools-web-search
웹 검색은 별도 API 사용량/비용을 발생시킵니다. 실검색 성공률과 지연은 이 환경에서 측정하지 않았습니다.
DOCENT_WEB_SEARCH=false는 검색을 끄므로 TourAPI와 앱 자료만 사용하게 됩니다.

## Android 요청 보완 (현재 Kotlin 호출 소스가 없어 서버만 수정됨)
기존 요청도 받습니다. 정확도를 위해 region에 실제 시·군·구, address에 장소 전체 주소를 보내세요.
region이 없을 때 서버가 장소명만 보고 지역을 임의로 만들어 넣지는 않습니다.
추천 엔진에서 실제로 사용한 선택 기준을 recommendationReasons에 넣으세요.
그 기준이 없으면 확인된 특징으로 방문 가치를 설명하며 실제 선정 이유를 알고 있다고 주장하지 않습니다.
예시 데이터 형식 (사실 확인용 데이터가 아닌 요청 예시):

```json
{
  "placeName": "실제 선택된 장소명",
  "region": "실제 시·군·구",
  "address": "실제 장소 주소",
  "topic": "auto",
  "userInterests": "풍경 감상과 지역 음식",
  "recommendationReasons": ["선택한 풍경 취향과 일치"],
  "factSnippets": ["장소 데이터에서 확인한 특징"],
  "sourceName": "앱이 사용한 장소 데이터 출처"
}
```

- topic은 auto를 권장합니다. 기존 history/food 등도 강제 조건이 아닌 우선 소재로 취급합니다.
- factSnippets는 신뢰할 수 있는 앱 데이터만 넣으세요. 서버가 이 내용을 독립 검증하지는 않습니다.
- '가까움'은 실제 거리 계산, '취향 일치'는 실제 추천 결과가 있을 때만 전달하세요.
- sources의 title/url을 카드 아래 클릭 가능한 출처로 보여주세요. storyText는 음성용으로 URL을 읽지 않습니다.
- storyScope=region이면 ‘지역 이야기’, mixed이면 ‘장소·지역 이야기’ 표시를 권장합니다.
- ready만 정상 이야기로 장기 보관하고 나머지는 실패 캐시에 넣지 마세요.
- HTTP 502와 temporarily_unavailable은 재시도 가능 오류입니다. 자동 무한 재시도는 하지 마세요.
- 순차 검색 때문에 최초 응답이 길어질 수 있습니다. 개발 검증 중에는 HTTP 호출 전체 대기 시간을 240초 정도로 설정해 타임아웃과 자료 부족을 혼동하지 마세요.
  이는 운영 UX 목표가 아닙니다. 운영에서는 추천 카드 표시 시 미리 생성하고 로딩/취소 UI를 제공하는 방식이 필요합니다.
- 검색 성공 자료는 프로세스 내 최대 256개, 30분 캐시. TourAPI 성공은 1시간 캐시.
  실패/빈 응답은 캐시하지 않습니다. 완성 이야기 캐시는 서버에 추가하지 않아 취향 변경이 바로 반영됩니다.

## 응답 진단
- retrievalDiagnostics.tourapi: matched, no_results, ambiguous, unavailable 등.
- retrievalDiagnostics.placeSearch / regionSearch: matched, matched_cached, no_evidence,
  identity_unconfirmed, search_unavailable, search_invalid_response, search_incomplete, search_unverified_sources.
- status=ready: 모델이 구체적인 방문 가치 근거가 충분하다고 판단함.
- status=insufficient_evidence: 기본 소개 수준 또는 근거 부족.
- status=temporarily_unavailable: 조회 실패로 자료를 확보하지 못함.

## 검증 및 한계
`python -m unittest -v` : 23개 모의 테스트 통과.
이름/지역 매칭, 중복, 캐시, 임의 장소 공통 경로, 역사→풍경 전환 응답 처리,
지역 fallback, 지역 추측 방지, 검색 장애 구분, 실제 추천 기준 전달,
기본 소개 성공 오표시 방지, 출처 URL·근거 ID 검증을 확인했습니다.

실제 API 키를 사용한 통합 호출, 실제 장소별 응답의 사실성, Android 직렬화·화면·TTS는 미검증입니다.
웹 검색 결과에 등장한 URL인지 검사하지만 해당 문장이 그 페이지에 의해 뒷받침되는지,
동일 장소인지, 추천 설명이 충분한지는 모델 판단도 포함합니다. 사실성 100% 보장이 아닙니다.
모든 장소에 같은 절차를 적용한다는 뜻이며, 모든 장소의 자료 확보를 보장한다는 뜻은 아닙니다.
서버는 기존 개발용 구성입니다. 인증이나 배포 설정을 변경하지 않았습니다.

## 실제 연결 확인
1. 익숙한 문화유산, 해변/공원, 일반 카페/식당, 동명 지점, 정보가 적은 장소를 각각 요청.
2. 실제 주소와 일치하는 출처인지, 확인되는 특징으로 방문 이유를 설명하는지 확인.
3. 역사 자료가 없더라도 음식/풍경으로 설명이 이어지는지 확인.
4. 지역 이야기가 가게의 유래/메뉴/전망으로 둔갑하지 않았는지 확인.
5. 키/권한/시간 초과 실패 시 ‘이 장소에는 이야기가 없다’로 표시되지 않는지 확인.
6. 취향 변경, 재요청, 화면 출처 클릭, 음성 재생까지 확인.
