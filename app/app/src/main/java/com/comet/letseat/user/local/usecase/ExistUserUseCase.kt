package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.repository.UserRepository

/**
 * 유저 정보가 있는지 확인하는 유스케이스
 * @property userRepository 유저 정보가 담긴 레포지토리
 * @see UserRepository.isUserExists
 */
class ExistUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke() : Boolean {
        return userRepository.isUserExists()
    }
}