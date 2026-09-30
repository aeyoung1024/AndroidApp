# TabApp

Galaxy Tab Active3 용 Kotlin + Jetpack Compose 태블릿 앱.

## 구성
- **로그인 화면** → 로그인 성공 시 **메인 화면**(6개 기능 버튼) → 각 **기능 화면**
- 기능: 입고 · 불출 · 이동 · 사외반출 · 재고현황 · 동기화
- 가로/세로 모두 지원 (가로 기본 사용)
  - 로그인: 가로 = 좌측 로고 / 우측 입력폼, 세로 = 상단 로고 / 하단 입력폼
  - 메인: 가로 = 3열 × 2행, 세로 = 2열 × 3행 (스크롤 없이 한 화면)
  - 회전해도 입력값·로그인 상태 유지 (ViewModel / rememberSaveable)
- 데모 계정: `admin` / `1234`

## 실행
Android Studio 에서 이 폴더를 열고 Gradle Sync 후 ▶ Run.
(최소 Android 8.0 / API 26, 대상 API 35)

## 수정 위치
| 내용 | 파일 |
| --- | --- |
| 6개 버튼 이름·아이콘·색상 | `app/src/main/java/com/example/tabapp/data/Feature.kt` |
| 입고 화면 | `ui/feature/InboundScreen.kt` |
| 불출 화면 | `ui/feature/IssueScreen.kt` |
| 이동 화면 | `ui/feature/TransferScreen.kt` |
| 사외반출 화면 | `ui/feature/ExternalOutScreen.kt` |
| 재고현황 화면 | `ui/feature/InventoryScreen.kt` |
| 동기화 화면 | `ui/feature/SyncScreen.kt` |
| 로그인 검증(서버 연동) | `data/AuthRepository.kt` |
| 화면 이동(네비게이션) | `navigation/AppNavHost.kt` |
| 색상 테마 | `ui/theme/Theme.kt` |
