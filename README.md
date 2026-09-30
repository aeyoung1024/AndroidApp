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

## 입고
1. **발주 리스트**: 상단 조회 기간 버튼(1개월 / 3개월 / 6개월 / 1년 이상, 발주일 기준), 검색(발주번호·품목·거래처), 미입고/입고완료/전체 필터
2. 발주 선택 → **입고 등록** 화면에 발주량만큼 레코드 자동 생성
3. 레코드마다 **입고 위치 → 실린더 번호**를 스캐너로 입력
   - 스캔 값 끝의 Enter(또는 Tab)를 받으면 다음 빈 칸으로 자동 이동
   - 실린더 번호 중복(같은 발주 내 / 이미 입고된 번호) 시 경고 후 다시 스캔
   - "입고 위치 동일 적용" 스위치: 첫 위치를 나머지 레코드에 자동 입력 → 실린더만 연속 스캔
4. 모든 레코드 입력 시 **입고 저장** → 발주 상태가 입고완료로 변경 (입고완료 발주는 조회만 가능)

> 스캐너는 **키보드 입력 방식(HID / 키보드 웨지)** 으로 설정하고, 스캔 후 **Enter 접미사**를 붙이도록 설정하세요.
> (삼성 Knox Capture 사용 시 "키 입력(Keystroke)" 출력 + Enter 접미사)

## 디자인
- Samsung Blue(#1428A0) 기반 One UI 스타일: 밝은 회색 배경 + 흰 카드 + 큰 라운드, 다크 모드 지원
- 로그인: 파란 그라데이션 배경 + SAIT 로고 + 흰색 로그인 카드
- 메인: SAIT 로고 헤더, 인사말, 기능 타일 6개

### SAIT 공식 로고 넣기
`app/src/main/res/drawable/` 폴더에 아래 이름으로 로고 파일을 넣으면 코드 수정 없이 자동 적용됩니다.
(파일이 없으면 "SAIT" 텍스트 로고로 표시)

| 파일명 | 용도 |
| --- | --- |
| `sait_logo.png` (또는 .webp / 벡터 .xml) | 밝은 배경용 (메인, 상단바) |
| `sait_logo_white.png` (선택) | 파란 배경용 (로그인). 없으면 기본 로고를 흰색으로 표시 |

파일명은 영문 소문자·숫자·밑줄(_)만 사용할 수 있습니다.

## 실행
Android Studio 에서 이 폴더를 열고 Gradle Sync 후 ▶ Run.
(최소 Android 8.0 / API 26, 대상 API 35)

## 수정 위치
| 내용 | 파일 |
| --- | --- |
| 6개 버튼 이름·아이콘·색상 | `app/src/main/java/com/example/tabapp/data/Feature.kt` |
| 입고 - 발주 리스트 | `ui/inbound/InboundOrderListScreen.kt` |
| 입고 - 등록(스캔 입력) | `ui/inbound/InboundRegisterScreen.kt`, `InboundRegisterViewModel.kt` |
| 입고 - 데이터(샘플 발주, 저장) | `data/inbound/InboundRepository.kt` |
| 불출 화면 | `ui/feature/IssueScreen.kt` |
| 이동 화면 | `ui/feature/TransferScreen.kt` |
| 사외반출 화면 | `ui/feature/ExternalOutScreen.kt` |
| 재고현황 화면 | `ui/feature/InventoryScreen.kt` |
| 동기화 화면 | `ui/feature/SyncScreen.kt` |
| 로그인 검증(서버 연동) | `data/AuthRepository.kt` |
| 화면 이동(네비게이션) | `navigation/AppNavHost.kt` |
| 색상 테마 | `ui/theme/Theme.kt` |
