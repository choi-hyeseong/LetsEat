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
def delete_function(request):
    remote = MySQLdb.connect(
        host=os.environ.get("DB_HOST", "localhost"),
        user=os.environ.get("USER", "user"),
        password=os.environ.get("PASSWORD", "root"),
        database=os.environ.get("DB", "test")
    )
    cur = remote.cursor()
    request_json = request.get_json(silent=True)
    if "uuid" not in request_json:
      return json.dumps({"message" : "please provide uuid"}), 400
    # 사용자 uuid
    uuid = request_json["uuid"]

    cur.execute("SELECT * FROM history where userId = \"%s\"" % uuid)
    histories = cur.fetchall()  # 이력 가져오기

    # 매핑
    for history in histories:
        history_id = history[0]  # 해당 히스토리의 고유 id
        # 각 연관관계 테이블만 삭제 - 나머지는 고유하므로 재활용 가능
        cur.execute("DELETE FROM user_category where historyId = %d" % history_id)
        cur.execute("DELETE FROM user_menu where historyId = %d" % history_id)
        cur.execute("DELETE FROM history where id = %d" % history_id) # 유저 히스토리 삭제
    remote.commit()
    cur.close()

    return json.dumps({"isSuccess" : True}), 200
