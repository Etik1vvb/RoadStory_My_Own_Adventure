# Road Story | 나만의 여행을 만들어가는 차량용 AI 동반자

> **목적지까지 가는 길도 여행이 되도록.** Road Story는 사용자의 취향, 여행 목적, 이동 상황 및 차량 정보를 반영해 주변 장소를 제안하고, 내비게이션과 AI 도슨트 안내로 연결하는 **Pleos Connect 기반 차량용 여행 추천 앱**입니다.

**앱 이름:** Road Story
**팀명:** 현대차 야호
**AI 어시스턴트:** 모아(MOA)
**프로젝트:** 2026 현대자동차그룹 차량용 앱 개발 공모전 출품작
**개발 단계:** Pleos Connect 에뮬레이터 기반 프로토타입

## 1. 프로젝트 소개

일반적인 길찾기는 사용자가 목적지를 먼저 정해야 시작됩니다. Road Story는 목적지를 향해 이동하는 중에도 여행자의 선택과 상황을 고려해 **다음에 들러볼 장소를 제안**합니다.

- **취향 기반 여행 준비:** 여행자 프로필, 선호 장소, 동행인, 여행 목적과 조건 설정
- **이동 중 장소 추천:** Kakao Local 장소 후보를 다중 요소 점수로 평가하고 추천 카드로 제공
- **차량 맥락 반영:** 현재 위치와 남은 주행 가능 거리 등 Pleos 차량 상태를 추천 경험에 활용
- **내비게이션 연계:** 선택한 장소를 Pleos 내비게이션의 목적지 또는 경유 흐름으로 연결
- **AI 도슨트:** 관광 정보와 생성형 AI를 활용해 장소의 역사·문화·음식 이야기를 제공
- **음성 인터페이스:** 음성 명령 및 Pleos TTS를 이용한 안내 기능
- **여행 상태 관리:** 진행 중인 여행 재개, 새로운 여행 시작, 이전 여행 기록 확인

## 2. 주요 사용 흐름

1. **프로필 선택:** 바다, 풍경, 오래된 맛집 등 자신의 선호를 설정합니다.
2. **여행 조건 입력:** 동행인, 방문 목적, 관심 장소와 여행 상황을 선택합니다.
3. **첫 목적지 결정:** 장소를 검색하고 여행을 시작하면 내비게이션과 연결됩니다.
4. **주행 중 추천:** 현재 위치와 여행 맥락을 반영한 추천 카드를 확인합니다.
5. **장소 선택 및 안내:** 관심 장소를 선택해 길 안내로 연결하고, AI 도슨트 이야기를 읽거나 듣습니다.
6. **상황 변화 반영:** 남은 주행 가능 거리 등의 차량 정보를 활용해 휴식·보충 장소 추천을 보조합니다.
7. **여행 관리:** 여행을 이어서 진행하거나 새로운 여행을 준비합니다.

> 일부 추천 및 음성 기능은 장소 검색 결과, 이용 권한, 네트워크, 외부 API 응답, 차량 SDK 상태에 따라 실행 조건이 달라집니다.

## 3. 기술 구성

| 구성 요소 | 사용 기술 |
| --- | --- |
| 앱 | Kotlin, Jetpack Compose, Android Automotive 환경 |
| 차량 데이터 | Pleos Connect Vehicle SDK |
| 내비게이션 | Pleos NaviHelper |
| 음성 출력 | Pleos TTS |
| 장소 검색 | Kakao Local API |
| 관광 정보 | 한국관광공사 TourAPI |
| AI 도슨트 | Python FastAPI, OpenAI API |
| 소스 관리 | Git / GitHub |

**추천 처리 흐름**

```text
프로필·여행 조건 + 현재 위치·차량 상태
                ↓
        Kakao Local 장소 후보
                ↓
      중복·거절 장소 후보 제외
                ↓
  프로필·목적·경험·기분·접근성·다양성 평가
                ↓
         추천 카드 및 안내
                ↓
        장소 선택 → 내비게이션
                ↓
        필요 시 AI 도슨트·TTS
```

현재 제출용 운영 설정의 추천 카드 최소 점수는 **60점**, 자동 음성 추천 최소 점수는 **70점**입니다. 자동 음성 추천은 최대 빈도를 제한하도록 쿨다운을 두었으며, 개발용 테스트 모드는 비활성화되어 있습니다. 추천 점수는 확률이나 객관적 관광지 평가가 아닌 **앱 내부의 상대적 적합도 지표**입니다.

## 4. 개발 및 테스트 환경

| 항목 | 환경 |
| --- | --- |
| 차량 에뮬레이터 | **Pleos Connect v2.1.0** |
| Android | **14 / API 34** |
| CPU 아키텍처 | x86_64 에뮬레이터 |
| 차량용 화면 | 시연 환경의 차량 디스플레이 기준 **2560 × 1440**, 가로형 레이아웃 |
| 개발 도구 | Android Studio, Gradle, PowerShell (Windows) |
| Release 패키지 | `com.example.app` |

별도로 구성한 `pleos_release_test` 에뮬레이터에서 **서명된 Release APK 설치 및 실행**, 내비게이션 표시, FastAPI 연동에 의한 AI 도슨트 텍스트 생성을 확인했습니다. APK 서명은 Android APK Signature Scheme v2 기준 검증을 통과했습니다. 이러한 확인은 에뮬레이터 환경의 검증이며, 실제 차량에서의 동작을 보증하지 않습니다.

## 5. 저장소 구조

```text
RoadStory_My_Own_Adventure/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/app/
│       │   ├── MainActivity.kt
│       │   ├── ui/
│       │   └── vehicle/
│       └── res/
├── MOA_Docent_FastAPI_Starter/
│   ├── app.py
│   ├── tourapi_client.py
│   ├── requirements.txt
│   └── README.md
├── apk/
│   └── 현대차 야호_RoadStory_release.apk
├── gradlew.bat
└── README.md
```

## 6. 설치 및 실행

### 6-1. 저장소 준비

```powershell
git clone https://github.com/Etik1vvb/RoadStory_My_Own_Adventure.git
cd RoadStory_My_Own_Adventure
```

Android Studio에서 프로젝트를 열어 Gradle 동기화를 진행합니다. **Pleos Connect SDK 및 동일한 차량용 에뮬레이터 환경**이 필요합니다. 일반 휴대전화용 Android 에뮬레이터에서는 차량 관련 기능이 제한될 수 있습니다.

### 6-2. 사전 설정 및 앱 빌드

Kakao Local 기능을 자체 빌드에서 사용하려면 프로젝트 루트의 `local.properties`에 유효한 키를 설정해야 합니다. Android SDK 경로 등 기존 설정은 유지합니다.

```properties
KAKAO_REST_API_KEY=YOUR_OWN_KAKAO_REST_API_KEY
```

디버그 APK 빌드:

```powershell
.\gradlew.bat :app:assembleDebug
```

제출된 서명 Release APK를 설치할 때는 기존 Debug 앱과 **서명이 다르므로**, 새 Pleos 테스트 에뮬레이터를 이용하는 것이 안전합니다.

```powershell
adb devices
adb -s emulator-5554 install -r "apk/현대차 야호_RoadStory_release.apk"
adb -s emulator-5554 shell am start -W -n com.example.app/.MainActivity
```

`emulator-5554`는 예시입니다. `adb devices`에 표시된 실제 테스트 기기 ID로 변경해 실행합니다.

### 6-3. AI 도슨트용 로컬 서버

AI 도슨트는 **별도 FastAPI 서버**에 의존합니다. 백엔드 실행에 필요한 Python 패키지와 환경변수는 `MOA_Docent_FastAPI_Starter/README.md`를 우선 참고합니다.

Windows PowerShell 예시:

```powershell
cd MOA_Docent_FastAPI_Starter
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
# 백엔드 README에 따라 .env 등 로컬 환경변수 설정
.\.venv\Scripts\python.exe -m uvicorn app:app --host 127.0.0.1 --port 8000
```

서버 실행 여부는 PC 브라우저에서 `http://127.0.0.1:8000/docs`로 확인합니다.

서버를 실행한 상태에서 **다른 PowerShell 터미널**을 열고 연결합니다.

```powershell
adb -s emulator-5554 reverse tcp:8000 tcp:8000
adb -s emulator-5554 reverse --list
```

이 연결은 **개발 PC와 USB/ADB로 연결된 에뮬레이터의 로컬 테스트 방식**입니다. 다른 PC나 실제 차량에서 APK만 설치한다고 AI 도슨트 서버에 자동으로 접속하지는 않습니다. 독립적인 실행을 위해서는 별도 서버 배포 및 HTTPS 연결 구성이 필요합니다.

## 7. APK 및 시연 자료

- **Release APK:** [`apk/현대차 야호_RoadStory_release.apk`](apk/현대차%20야호_RoadStory_release.apk)
- **소스코드:** [RoadStory_My_Own_Adventure](https://github.com/Etik1vvb/RoadStory_My_Own_Adventure)
- **시연 영상:** 공모전 접수 자료로 별도 제출

APK의 실제 파일명과 위치는 제출 시점의 GitHub 커밋을 기준으로 확인합니다.

## 8. 보안 및 제약사항

- `.env`, `local.properties`, 키스토어(`.jks`), 서명키 비밀번호 등 **비밀정보는 저장소에 포함하지 않습니다**.
- `local.properties`의 값이 Gradle을 통해 APK에 주입되는 경우, 해당 값은 **APK에서 추출 가능**할 수 있습니다. 배포 환경에서는 API 제공자의 키 제한 기능 또는 서버 측 중계를 고려해야 합니다.
- Release APK는 난독화 관련 Pleos SDK 호환성 문제를 피하도록 빌드된 시연용 산출물입니다.
- 추천 결과 및 AI 설명은 외부 API와 인터넷 연결 상태에 영향을 받습니다. 장소 사실관계는 원천 데이터와 모델 응답에 따라 차이가 발생할 수 있습니다.
- 현재 추천 엔진은 Activity 생명주기에 연결된 구성으로, OS 수준의 독립된 상시 실행 서비스와 동일하지 않습니다.
- 실제 주행 환경에서의 안전성, 권한, 서버 운영, 장애 복구 및 상용 배포 요건은 별도 검증이 필요합니다.

---

**Road Story**는 차량 이동 시간을 여행의 일부로 바꾸는 경험을 탐색하기 위해 제작된 공모전용 프로토타입입니다.
