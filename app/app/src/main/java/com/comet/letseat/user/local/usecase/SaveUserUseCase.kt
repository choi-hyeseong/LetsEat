package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.repository.UserRepository

/**
 * 유저 정보를 저장하는 유스케이스
 * @property userRepository 유저 정보를 관리하는 레포지토리
 * @see UserRepository.saveUser
 */
class SaveUserUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(userData: UserData) {
        userRepository.saveUser(userData)
    }
}