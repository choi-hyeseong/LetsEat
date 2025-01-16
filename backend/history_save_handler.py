import time
import os
import json
import functions_framework
import MySQLdb


# 테이블 구성 필요

"""
CREATE TABLE IF NOT EXISTS history(
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(255),
    statusCode VARCHAR(5),
    timeStamp BIGINT
);

CREATE TABLE IF NOT EXISTS category(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS menu(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS user_category(
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoryId INT,
    historyId Int,
    foreign key(categoryId) references category(id),
    foreign key(historyId) references history(id)
);

CREATE TABLE IF NOT EXISTS user_menu(
    id INT AUTO_INCREMENT PRIMARY KEY,
    menuId INT,
    historyId Int,
    foreign key(menuId) references menu(id),
    foreign key(historyId) references history(id)
);
"""
@functions_framework.http
def save_handler(request):
    remote = MySQLdb.connect(
        host=os.environ.get("DB_HOST", "localhost"),
        user=os.environ.get("USER", "user"),
        password=os.environ.get("PASSWORD", "root"),
        database=os.environ.get("DB", "test")
    )
    cur = remote.cursor()
    request_json = request.get_json(silent=True)
    if "uuid" not in request_json:
        return json.dumps({"message": "please provide uuid"}), 400
    if "status_code" not in request_json:
        return json.dumps({"message": "please provide status code"}), 400
    if "categories" not in request_json:
        return json.dumps({"message": "please provide categories"}), 400
    if "results" not in request_json:
        return json.dumps({"message": "please provide results"}), 400

    # 사용자 uuid
    uuid = request_json["uuid"]
    # 요청시간
    timestamp = int(time.time() * 1000)
    # 상태코드
    statuscode = request_json["status_code"]
    # 카테고리
    categories = request_json["categories"]
    # 결과
    results = request_json["results"]

    inserted_category_id = [] # db에 추가된 카테고리
    inserted_menu_id = [] # db에 추가된 id
    # 카테고리 추가. 이미 있는경우 ignore 됨
    for category in categories:
        # parmeterized query
        cur.execute("INSERT IGNORE INTO CATEGORY(name) VALUES(\"%s\")" % category)
        cur.execute("SELECT id FROM CATEGORY where name = \"%s\"" % category)
        # result
        selected_id = cur.fetchall()[0][0]
        inserted_category_id.append(selected_id)

    # 메뉴 추가
    for result in results:
        # parmeterized query
        cur.execute("INSERT IGNORE INTO MENU(name) VALUES(\"%s\")" % result)
        cur.execute("SELECT id FROM MENU where name = \"%s\"" % result)
        # result
        selected_id = cur.fetchall()[0][0]
        inserted_menu_id.append(selected_id)

    # history 추가
    cur.execute("INSERT INTO HISTORY(userId, statusCode, timeStamp) VALUES(\"%s\", \"%s\", %d)" % (uuid, statuscode, timestamp))
    # history id
    cur.execute("SELECT * FROM HISTORY WHERE timeStamp = %d" % timestamp)
    history_id = cur.fetchall()[0][0]

    # 연관 관계 테이블 추가
    for menuId in inserted_menu_id:
        cur.execute("INSERT INTO user_menu(menuId, historyId) values(%d, %d)" % (menuId, history_id))

    for category_id in inserted_category_id:
        cur.execute("INSERT INTO user_category(historyId, categoryId) values(%d, %d)" % (history_id, category_id))

    remote.commit()
    cur.close()
    return json.dumps({"message" : "success"}), 200
