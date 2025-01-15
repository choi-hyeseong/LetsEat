package com.comet.letseat.user.remote.user.usecase

import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import java.util.UUID

/**
 * 레포지토리 호출하는 유스케이스 - 유저 삭제
 */
class RemoteDeleteUserUseCase(private val remoteUserRepository: RemoteUserRepository) {

    suspend operator fun invoke(uuid: UUID) : Boolean {
        return remoteUserRepository.delete(uuid)
    }
}