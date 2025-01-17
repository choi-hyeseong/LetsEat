# 밥묵자
### 뭐 먹을지 모를때 AI가 추천해주는 오늘의 알찬 메뉴!
![Image](https://github.com/user-attachments/assets/e16f0464-b18c-479c-9260-8cd19afc1730) ![Image](https://github.com/user-attachments/assets/0381ffd1-6958-40a4-bd23-933e56565ff0)

### ✏️ 구성 방법
* 백엔드 : https://github.com/choi-hyeseong/LetsEat/tree/main/backend
* 프론트엔드 (안드로이드) : local.properties 파일 구성하기, ARM 기기 필요 (x86 가상머신 X - kakao map api로 인해..)
```properties
kakao.api_key=NATIVE_API_KEY
kakao.web_api_key="WEB_API_KEY"
server.url="BACKEND_BASE_URL(배포시)"
```
다음과 같이 properties 파일을 구성하고 빌드하시면 되겠습니다.<br/>
https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/
해당 URL을 참고하셔서 앱 해시와 키값을 적절히 등록해주시면 되겠습니다.!

### 🖥️  관련 자료
* Figma
![Image](https://github.com/user-attachments/assets/e2df40ad-f6d7-46c1-971f-bdfa312a0b97)
  https://www.figma.com/design/kd6jtE6GRJnhHLEp849Kiw/%EB%B0%A5%EB%AC%B5%EC%9E%90-%EB%94%94%EC%9E%90%EC%9D%B8
* ERD
  ![Image](https://github.com/user-attachments/assets/f85f46a4-84c8-48cf-abe7-8710c9409214)

### 📹 시연 영상
https://www.youtube.com/watch?v=IFGFfmQqvGM
### 📝 구조 및 설계
#### 안드로이드
* MVVM Clean Architecture 준수
* View(Activity)를 제외한 나머지 부분에 대한 Statement Coverage 90% 이상 달성

![Image](https://github.com/user-attachments/assets/009b884c-a054-47c1-9f7d-9f031340fe1d)
![Image](https://github.com/user-attachments/assets/3622977f-45c3-41b5-8494-8c528064f04d)

나머지 cover 되지 못한 부분은 Activity / Fragment와 같이 runtime에서 테스트가 필요한 부분입니다.

Domain / Data Layer와 같은 Dao, Repository, UseCase, ViewModel 부분에 있어선 테스트 코드를 작성 및 통과하는것을 확인했습니다.
그리고, 구문 커버리지라고 하더라도, 최대한 Condition / Decision Coverage를 충족할 수 있게 여러 조건을 넣어가며 테스트를 수행했습니다!
* 동시성으로부터 안전한 DataStore 적용
<br/>https://velog.io/@choi-hyeseong/SharedPreference%EB%8A%94-%EC%93%B0%EB%A0%88%EB%93%9C-%EC%95%88%EC%A0%84%ED%95%A0%EA%B9%8C
<br/>예전에 작성한 SharedPreference에 관한 글에 따라 이번 프로젝트에서는 DataStore를 사용했습니다.
* 주석 열심히 달기.. - 노력은 열심히 했었습니다
* 간략화
<br/>코드를 작성하다 보면 중복되는 코드가 상당히 많았습니다. 카테고리 선택화면과 결과 선택화면등등
<br/>이 부분을 하나의 클래스로 만들고, 간략화 한 뒤, 확장 가능성이 있을것으로 보이면 인터페이스를 구성할 수 있게 구성하는 등 여러 방법을 사용했습니다.(AbstractDialog, ViewStateViewModel)
#### 백엔드
* FaaS를 이용한 서버리스 아키텍처 활용 - Python
<br/>요즘 대세인 AWS 람다와 같은 서버리스 아키텍처로 서버를 구성했습니다.
<br/>현재 로직상 서버와 정기적인 통신이 이루어질 필요는 없으므로, 사용자가 메뉴 추천 요청, 기록 불러오기, 유저 삭제등 이벤트가 발생할때만 작동하면 되는 방식으로 구성했습니다.

#### DB
* Category와 Menu의 N:M 테이블 구성
그냥 DB에 json등과 같이 평문 저장하는 방법도 있지만, 추후 사용자의 검색 기록을 분석해서 특정 카테고리의 검색에 따른 연관된 메뉴를 분석할때 연결지으면 좋겠다 생각이 들어
중간테이블을 활용한 N:M 관계로 구성하였습니다.
<br/>예를 들어 "매운", "짠" 카테고리로 검색을 했을때 "라멘"이 검색되어 저장된다면, 추후 유저 정보가 충분히 쌓였을경우, 매운 카테고리에 해당하는 이력을 쿼리 한뒤, 해당 이력의 결과 메뉴를 분석하여 AI 파인튜닝등에 사용할 수 있을것으로 사료됩니다. (유저에게 사전 고지는 필요.)

### ☠ 트러블 슈팅
#### KakaoMap API의 x86 가상머신 미지원
내부적으로 jni의 so 파일을 로드하는데, 이 라이브러리가 x86 가상머신에서는 지원되지 않아 로드중 실패되는 일이 발생했습니다. 이 부분은 어쩔 수 없어 가상머신 대신 실 기기를 사용하여 해결하였습니다.
#### AWS Lambda 라이브러리 충돌
FaaS에 AWS대신 GCP의 Cloud Run을 사용한 이유이기도 한데, AWS에서 사용하는 Python 3.12버젼의 라이브러리를 Layer에서 불러올때 arm64 버전의 라이브러리를 필요로 했습니다.
그래서 pip 설치시 arm64 버전으로 받아서 올려줬는데도 지속적으로 라이브러리를 찾을 수 없다고 해서 requirements.txt파일로 배포시 라이브러리를 불러오는 gcp의 cloud run을 사용하게 되었습니다.

물론 docker로 빌드 후 배포하는 방식도 사용가능하지만, 일단은 gcp로 업로드하여 해결했습니다.
#### N:M 연관관계
이전 Spring Boot의 JPA를 이용할땐 그래도 연관관계 매핑만 해주면 ORM에 의해 객체로 불러와주고 저장도 잘 해줬는데,
raw하게 쿼리를 날리고 매핑하는 과정은 익숙하지 않았습니다. 그래도 inner join을 이용해서 테이블간 쿼리를 잘 날려 해결했습니다!

#### VM 코드 수백줄
위 앱 구조상 MapActivity에서 대다수의 기능이 처리되는데, 대부분 dialog를 이용해서 정보를 전달하기 때문에 Viewmodel 또한 각 다이얼로그에 띄워줄 livedata와 처리 메소드를 갖고 있어야 했습니다.
처음에는 MapViewModel이 전부 정보를 관리하는것이 맞을듯 하여 로직을 해당 클래스에 다 넣어서 작성했는데.. 하다보니 수백줄이 넘어갔습니다.

아무리 mvvm이더라도 수많은 코드가 집약된다면 관리하기 어려워지기 때문에 해결책을 찾던중, DialogFragment또한 Fragment이므로 고유한 ViewModel을 할당해줄 수 있다고 했습니다.
물론 좋은 방법은 아니겠지만, 기존 방식보단 각 다이얼로그에 걸맞게 코드를 분산시킬 수 있기 때문에 각 다이얼로그별 뷰모델을 생성해주어 해결했습니다.

이 부분은 ChatGPT도 그렇고 이벤트 소싱이나 argument와 result를 이용해서 하는 방법이 좋다는 의견이 있어, 일단 메인이 되는 MapViewModel과 다이얼로그의 뷰모델과의 통신은 sharedViewModel 보단, argument와 fragment result를 이용했습니다. 

#### DB 요청 지연
현재 촬영된 영상에도 나타나는데, 검색 이력 로드, 유저 정보 삭제와 같은 쿼리에 상당한 지연이 나타났습니다.
원인을 찾은 결과... 무료 db를 제공하는 서비스가 상당히 느려서 생긴 문제라.. db서비스가 나아진다면 해결될 문제로 보입니다. (원래 1초내로 끝나는거라 로딩 화면도 적용하지 않았습니다.. ㅎㅎ..)
