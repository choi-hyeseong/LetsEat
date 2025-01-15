package com.comet.letseat.user.remote.user.model

import java.io.Serializable
import java.util.UUID

/**
 * 유저 검색 결과 정보 - 다이얼로그로 전달하기 위해 Serializable 구현
 * @property uuid 사용자의 uuid
 * @property timeStamp api 호출 시간
 * @property category 사용자가 선택한 카테고리
 * @property response 응답 결과
 * @property statusCode 응답 코드
 */
data class UserHistory(val uuid : UUID, val timeStamp : Long, val category : List<String>, val response : List<String>, val statusCode : String) : Serializable