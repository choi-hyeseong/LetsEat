package com.comet.letseat.user.remote.user.api

import com.comet.letseat.user.remote.user.dto.DeleteResponse
import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.dto.UserRequest
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    // 유저 정보 삭제. delete매핑은 body가 없음
    @POST("delete_user-1")
    suspend fun delete(@Body request : UserRequest) : ApiResponse<DeleteResponse>

    // 유저의 검색 이력 확인
    @POST("load_history-1")
    suspend fun getHistory(@Body request : UserRequest) : ApiResponse<HistoryResponse>
}