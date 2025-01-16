import MySQLdb
import functions_framework
import os
import json



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

# history 이력 가져오기
@functions_framework.http
def history_function(request):
    remote = MySQLdb.connect(
        host=os.environ.get("DB_HOST", "localhost"),
        user=os.environ.get("USER", "user"),
        password=os.environ.get("PASSWORD", "root"),
        database=os.environ.get("DB", "test")
    )
    cur = remote.cursor()
    request_json = request.get_json(silent=True)
    # 사용자 uuid
    if "uuid" not in request_json:
        return json.dumps({"message" : "please provide uuid"}), 400
    uuid = request_json["uuid"]

    user_history = []
    cur.execute("SELECT * FROM history where userId = \"%s\"" % uuid)
    histories = cur.fetchall() # 이력 가져오기
    # 매핑
    for history in histories:
        id = history[0] # 해당 히스토리의 고유 id
        statusCode = history[2] #상태코드
        timeStamp = int(history[3]) #시간값
        categories = []
        menus = []

        # 카테고리 할당
        cur.execute("SELECT * FROM user_category where historyId = %d" % id)
        # N:M 연관 테이블 조회
        user_categories = cur.fetchall()
        for user_category in user_categories:
            category_id = user_category[1]
            cur.execute("SELECT * FROM category where id = %d" % category_id)
            category_result = cur.fetchall()
            categories.append(category_result[0][1]) # name 필드 가져옴

        # 메뉴 할당
        cur.execute("SELECT * FROM user_menu where historyId = %d" % id)
        # N:M 연관 테이블 조회
        user_menus = cur.fetchall()
        for user_menu in user_menus:
            menu_id = user_menu[1]
            cur.execute("SELECT * FROM menu where id = %d" % menu_id)
            menu_result = cur.fetchall()
            menus.append(menu_result[0][1])  # name 필드 가져옴

        user_history.append({
            "uuid" : uuid,
            "statusCode": statusCode,
            "timeStamp": timeStamp,
            "categories": categories,
            "menus": menus
        })
    cur.close()
    return json.dumps({"histories": user_history}, ensure_ascii=False), 200
