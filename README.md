# Usungsoft RFID Mobile Apps Helper Library
- Common functions (parse, etc..)
- Common views

# Reference Worldwide
- publish is [Bintray](https://bintray.com) => X 사용안함
- publish is [Jitpack](https://github.com/usungsoft/usLibrary)

# Bintray Publish API Key. => X 사용안함
- publish api key: d0e22475750b064f72e56d27056e9f6f5ebefdd7
- publish user name: usungsoftdev

# Publish
- [배포방법](https://sjh1253.tistory.com/entry/Android-Open-Source-Library-%EB%B0%B0%ED%8F%AC)

# NDK 적용
- File -> Project Structure -> SDK Location -> Android NDK location 정보 입력 (설치된 NDK 위치 잡아줌.)
- 2020.09.14: 현재 .dll이 32bit 로 release 된거라 안드에서 사용을 못 함. 주석처리 해놓음.

# 상품찾기 전용 게이지 프로그레스 연결 방법
- Bluebird.java 에 handler RFCmdMsg.READ | RFCmdMsg.INVENTORY_CUSTOM_READ (FNF/SAMSUNG) onScannerFind() if 구문 아래에
- ScannerHelper.java 에 추가한 onRssiCalculate 메소드를 호출하여 인터페이스 연결한다.
- onRssiCalculate()의 인자는 double Type 이고, 블루버드로 부터 전달받은 massage 에서 파싱한다.
- ex)
      > FNF / SAMSUNG : Double.parseDouble(((String) message.obj).substring(((String) message.obj).indexOf(":") + 1, ((String) message.obj).indexOf("^"))) ==> -90.2
      > WIVIS / LEENHAN / K2 .. : tagValues[1].substring(tagValues[1].indexOf(":") + 1)
- 해당 수평 프로그레스바를 사용하는 프래그먼트 클래스에서는 onRssiCalculate()를 오버라이딩 하고,
- UsHorizontalProgress 객체의 updateProgress(double rssi)를 적어준다.
- Cycle: 건 프레스 - Bluebird.java Handler Message () - onScannerFind()의 결과가 true - ScannerHelper.onRssiCalculate(double rssi) - 해당 프래그먼트 - UsHorizontalProgress Update
-onScannerStop() 에 mProgress.clearProgress() 호출하면, Animation 적용하여 progress가 0으로 감소된다.


# 2023.09.01 New
- 클라우드 기본 RFID 디코딩 네이티브 소스 추가
- cpp 소스는 푸시되지 않음 (보안상 - svn에서 관리)

# Release aar 배포방법
- variants : release
- clean project
- reBuild project
- app/src/build/outputs/aar 아래 app-release.aar 을 usLibrary.aar 로 변경
- 해당 .aar 파일로 사용할 안드로이드 프로젝트 libs 에 붙여넣고 디펜던시 추가 후 사용

# cpp native 코드가 변경 됐을 경우
- 변경 후 release build project
- build/intermediates/cmake/release/obj 아래 4개 폴더 복사
- jniLibs 아래에 붙여넣고 다시 reBuild
- 배포 시 위 배포방법대로

# 디코딩 호출
```
RfidUtils.Default rfidConverter = RfidUtils.Default.getInstance();
rfidConverter.decodeRfid("RFIDCODE");
```