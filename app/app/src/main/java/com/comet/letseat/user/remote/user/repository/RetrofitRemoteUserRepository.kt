package com.comet.letseat.user.remote.user.repository

import com.comet.letseat.user.remote.user.api.UserAPI
import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.dto.UserRequest
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.getOrNull
import java.util.UUID

// retrofit을 이용한 유저 레포지토리
class RetrofitRemoteUserRepository(private val userAPI: UserAPI) : RemoteUserRepository {

    override suspend fun delete(uuid: UUID): Boolean {
        val result = userAPI.delete(UserRequest(uuid)).getOrNull()
        return if (result == null) // api 호출 실패시
            false
        else
            result.isSuccess
    }

    override suspend fun getUserHistory(uuid: UUID): ApiResponse<HistoryResponse> {
        return userAPI.getHistory(UserRequest(uuid))
    }
}