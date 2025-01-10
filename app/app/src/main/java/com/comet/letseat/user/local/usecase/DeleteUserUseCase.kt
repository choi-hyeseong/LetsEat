package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.repository.UserRepository

/**
 * 유저 정보를 삭제하는 유스케이스
 * @see UserRepository.delete
 */
class DeleteUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke() {
        userRepository.delete()
    }
}