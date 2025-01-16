import os

from openai import OpenAI
import functions_framework
import json
import requests

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

client = OpenAI(
    api_key=os.environ.get("OPENAI_KEY", "Please provide OpenAI API KEY."))
system_role = "당신은 먹을 메뉴를 선택하지 못하는 사람들에게서 얻은 카테고리를 통해 메뉴를 추천해줍니다. 답은 꼭 파이썬 배열로만 해야합니다."

url = os.environ.get("SAVE_URL", "URL")


# gcp function에서 호출되는 함수 - 메뉴 추천
@functions_framework.http
def recommend_function(request):
    request_json = request.get_json(silent=True)

    # uuid가 없는경우
    if "uuid" not in request_json:
        return json.dumps({"message": "please provide user uuid" }), 400
    # 카테고리가 없는경우
    if "categories" not in request_json:
        return json.dumps({"message": "please provide user category" }), 400

    # 필드 할당
    uuid = request_json["uuid"]
    category = request_json["categories"]

    # open ai 호출
    prompt = str(category) + "에 해당하는 음식을 다른 말 필요없이 파이썬 배열로만 말해줘"
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": system_role},
                {"role": "user", "content": prompt}
            ]
        )
        # eval로 필드 할당
        menus = eval(response.choices[0].message.content)  # 리스트의 형태로 받아온 응답값
        status = "BE200"
    except:
        # 실패시
        menus = []
        status = "BE500"

    data = {
        "uuid": uuid,
        "categories": category,
        "status_code": status,
        "results": menus
    }
    # 저장 람다 핸들러 호출 (Requests)
    headers = {"Content-Type": "application/json"}
    requests.post(url, json = data, headers=headers)
    return json.dumps({"menus": menus}, ensure_ascii=False), 200