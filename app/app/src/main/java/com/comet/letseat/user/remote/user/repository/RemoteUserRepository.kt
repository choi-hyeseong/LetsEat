package com.comet.letseat.user.remote.user.repository

import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.skydoves.sandwich.ApiResponse
import java.util.UUID

// 원격 서버에 관리되는 유저 정보 레포지토리
interface RemoteUserRepository  {

    /**
     * 서버의 유저 정보를 삭제합니다.
     * @param uuid 사용자의 uuid
     * @return 성공 여부
     */
    suspend fun delete(uuid: UUID) : Boolean

    /**
     * 유저의 검색 이력을 가져옵니다.
     * @param uuid 사용자의 uuid
     */
    suspend fun getUserHistory(uuid: UUID) : ApiResponse<HistoryResponse>
}