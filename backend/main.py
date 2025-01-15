import ast
import time
from openai import OpenAI

"""
app 테스트용 더미 백엔드 - 곧 aws lambda로 이전 및 구축 예정
"""

client = OpenAI(api_key = "")
system_role = "당신은 먹을 메뉴를 선택하지 못하는 사람들에게서 얻은 카테고리를 통해 메뉴를 추천해줍니다. 답은 꼭 파이썬 배열로만 해야합니다."

from flask import Flask, request, jsonify

app = Flask(__name__)
"""
REQUEST
{
    "uuid" : "c3a4e6c4-7b2d-4fb8-9f11-af30b7316715",
    "categories" : ["단", "짠", "아침", "점심"... ]
}

RESPONSE
{
    "menus" : ["밥", "라면", "까르보나라", "그외등등", "맛있는거"]
}
"""
@app.route("/predict", methods=['POST'])
def predict():
    json = request.get_json()
    print(json["uuid"])
    print(json["categories"])
    prompt = str(json["categories"]) + "에 해당하는 음식을 다른 말 필요없이 파이썬 배열로만 말해줘"
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages = [
            {"role" : "system", "content" : system_role},
            {"role" : "user", "content": prompt}
        ]
    )
    print()


    return jsonify(menus = ast.literal_eval(response.choices[0].message.content))

@app.route("/user", methods=['DELETE'])
def delete():
    query = request.args
    print(query['uuid'])
    return jsonify({ "isSuccess" : True})

@app.route("/user/history", methods=['POST'])
def history():
    json = request.get_json()
    user = json["uuid"]
    print(user)

    return jsonify(histories = [
        {
            "uuid": user,
            "timeStamp": int(time.time() * 1000),
            "category": ["매운", "짠", "달콤한"],
            "response": ["까르보나라", "후라이드치킨"],
            "statusCode": "BE200"
        },
        {
            "uuid": user,
            "timeStamp": int(time.time() * 1000),
            "category": ["매운", "짠", "달콤한"],
            "response": [],
            "statusCode": "BE400"
        }

    ])




app.run(host = "127.0.0.1", port = 8080)