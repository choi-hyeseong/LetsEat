package com.comet.letseat.user.remote.user.usecase

import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import com.skydoves.sandwich.ApiResponse
import java.util.UUID

/**
 * 레포지토리 호출하는 유스케이스 - history 가져오기
 */
class GetUserHistoryUseCase(private val repository: RemoteUserRepository) {

    suspend operator fun invoke(uuid: UUID) : ApiResponse<HistoryResponse> {
        return repository.getUserHistory(uuid)
    }
}