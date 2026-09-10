# DuoFold — Galaxy Z Fold 4용 iPhone Duo 스타일 힌지 애니메이션 PoC

이 프로젝트는 Galaxy Z Fold 4에서 **힌지 각도 센서(TYPE_HINGE_ANGLE)** 값을 읽고, 그 값으로 커스텀 런처 화면의 애니메이션을 구동하는 Proof of Concept입니다.

중요: 이것은 One UI/SystemUI를 교체하는 앱이 아닙니다. 삼성의 실제 홈 화면/앱 화면을 가로채지 않고, **DuoFold 자체 화면을 홈 앱으로 사용**하는 방식입니다.

## 1. 준비물

- Windows PC
- 최신 안정판 Android Studio
- Galaxy Z Fold 4
- USB 케이블

## 2. 프로젝트 열기

1. 이 폴더의 압축을 풉니다.
2. Android Studio에서 **Open** → `DuoFold-Fold4` 폴더를 엽니다.
3. Gradle Sync가 끝날 때까지 기다립니다.
4. SDK Manager에서 Android 35 SDK가 없다면 설치합니다.

## 3. Fold 4 개발자 옵션 켜기

1. 설정 → 휴대전화 정보 → 소프트웨어 정보
2. **빌드번호**를 여러 번 눌러 개발자 옵션을 활성화
3. 설정 → 개발자 옵션 → **USB 디버깅** 켜기
4. PC 연결 후 Fold 4 화면에서 USB 디버깅 허용

## 4. 가장 쉬운 설치 방법

Android Studio 상단의 기기 선택에서 Galaxy Z Fold 4를 선택한 후 **Run ▶**을 누릅니다.

앱이 설치되면 홈 앱 선택 화면이 나타날 수 있습니다. `DuoFold`를 선택해 테스트합니다.

## 5. APK로 직접 설치

Android Studio 메뉴:

`Build → Build APK(s)`

생성 파일:

`app/build/outputs/apk/debug/app-debug.apk`

이 APK를 Fold 4로 옮긴 뒤 실행해 설치할 수도 있습니다.

ADB를 사용할 경우:

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 6. DuoFold를 기본 홈으로 지정

앱을 일반 앱으로 실행해 먼저 테스트해도 됩니다.

홈 화면으로 사용하려면:

`설정 → 애플리케이션 → 기본 앱 선택 → 홈 앱 → DuoFold`

기기/One UI 버전에 따라 메뉴 이름은 조금 다를 수 있습니다.

## 7. 사용법

- Fold 4를 접거나 펼치면 하단에 현재 힌지 각도가 표시됩니다.
- `TYPE_HINGE_ANGLE` 센서가 정상적으로 잡히면 `HINGE SENSOR 0°~180°` 형태로 표시됩니다.
- 길게 누르면 홈 앱 설정 화면으로 들어가 One UI Home으로 되돌릴 수 있습니다.

## 8. 기대할 수 있는 것

이 PoC의 핵심은:

`힌지 실제 움직임 → 센서 각도 → UI 애니메이션`

입니다.

따라서 단순히 `접힘 상태에서 다른 화면으로 전환`하는 것이 아니라, 펼치는 동안 애니메이션이 연속적으로 진행됩니다.

## 9. 현재 한계

- One UI Home 자체를 변형하지 않습니다.
- 실제 사용 중인 홈 화면 아이콘/위젯을 실시간으로 복제하지 않습니다.
- Fold 8에서 공개된 별도의 Presentation 기반 데모와 같은 **양쪽 내부 디스플레이 동시 렌더링 방식**을 Fold 4에서 동일하게 보장하지 않습니다.
- 현재 기본 화면은 데모용 그래픽입니다.

## 10. 다음 버전에서 가능한 개선

1. 사용자의 실제 홈 화면을 참고한 커스텀 레이아웃
2. 배경화면을 사용자가 지정
3. Galaxy 기본 아이콘 느낌을 반영
4. 힌지 각도별 블러/왜곡 강도 조절
5. 펼칠 때 화면 중앙의 깊이감과 그림자 강화
6. 접을 때 역방향 애니메이션 개선
7. One UI Home으로 복귀하는 전용 버튼 추가
