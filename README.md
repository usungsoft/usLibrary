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
> Bluebird.java 에 handler RFCmdMsg.READ | RFCmdMsg.INVENTORY_CUSTOM_READ (FNF/SAMSUNG) onScannerFind() if 구문 아래에
> ScannerHelper.java 에 추가한 onRssiCalculate 메소드를 호출하여 인터페이스 연결한다.
> onRssiCalculate()의 인자는 double Type 이고, 블루버드로 부터 전달받은 massage 에서 파싱한다.
> ex)
      > FNF / SAMSUNG : Double.parseDouble(((String) message.obj).substring(((String) message.obj).indexOf(":") + 1, ((String) message.obj).indexOf("^"))) ==> -90.2
      > WIVIS / LEENHAN / K2 .. : tagValues[1].substring(tagValues[1].indexOf(":") + 1)
> 해당 수평 프로그레스바를 사용하는 프래그먼트 클래스에서는 onRssiCalculate()를 오버라이딩 하고,
> UsHorizontalProgress 객체의 updateProgress(double rssi)를 적어준다.
> Cycle: 건 프레스 - Bluebird.java Handler Message () - onScannerFind()의 결과가 true - ScannerHelper.onRssiCalculate(double rssi) - 해당 프래그먼트 - UsHorizontalProgress Update
> onScannerStop() 에 mProgress.clearProgress() 호출하면, Animation 적용하여 progress가 0으로 감소된다.
