## 밥묵자 백엔드

### ✏️ 구조
![Image](https://github.com/user-attachments/assets/bfaae670-8dc8-47f6-ad5a-7906e21c4642)

* Google Functions Framework(Cloud Run)를 이용한 Serverless Architecture
* Python으로 구성
* OpenAI API 및 DB 연결구성

AI 요청부와 기록 저장부는 db 저장과 api 요청부의 처리 로직을 분리하기 위해 2개의 함수로 만들었습니다.

### 🖥️ 구성방법

* OpenAI API의 키가 필요합니다.
* MySQL로 구성된 서버의 HOST, DB, USER, PASSWORD가 필요합니다.


파일은 총 4개로 구성되어 있습니다.
* hisotry_load_handler.py
<br/>사용자의 검색 이력을 db에서 불러오는 함수입니다. 환경변수에 DB_HOST(IP), DB(DATABASE), USER, PASSWORD 4가지를 지정해주셔야 합니다.
* history_save_handler.py
<br/>사용자의 검색 이력을 db에 저장하는 함수입니다. 직접적으로 호출되지 않으며, openai 핸들러에서 호출됩니다.  환경변수에 DB_HOST(IP), DB(DATABASE), USER, PASSWORD 4가지를 지정해주셔야 합니다.
* user_delete_handler.py
<br/>사용자의 정보를 db에서 삭제하는 함수입니다. 환경변수에 DB_HOST(IP), DB(DATABASE), USER, PASSWORD 4가지를 지정해주셔야 합니다.
* openai_function_handler.py 
<br/>사용자의 카테고리를 기반으로 openai의 api를 호출해 메뉴를 가져오고, 이력 저장 요청을 하는 함수입니다.
환경변수에 OPENAI_KEY(ChatGPT API KEY), SAVE_URL(history_save_handler가 배포된 주소)가 필요합니다.
![Image](https://github.com/user-attachments/assets/00e4bb92-80f6-4124-ae9c-4bdb73565163)
구글 cloud run에 배포가 정상적으로 이루어졌을때 확인할 수 있습니다.


