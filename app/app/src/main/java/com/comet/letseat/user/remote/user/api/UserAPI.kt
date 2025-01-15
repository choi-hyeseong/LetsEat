package com.comet.letseat.user.remote.user.api

import com.comet.letseat.user.remote.user.dto.DeleteResponse
import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.dto.UserRequest
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

interface UserAPI {

    // 유저 정보 삭제. delete매핑은 body가 없음
    @DELETE("user")
    suspend fun delete(@Query("uuid") uuid : UUID) : ApiResponse<DeleteResponse>

    // 유저의 검색 이력 확인
    @POST("user/history")
    suspend fun getHistory(@Body request : UserRequest) : ApiResponse<HistoryResponse>
}