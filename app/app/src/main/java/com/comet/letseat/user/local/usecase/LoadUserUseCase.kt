package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.repository.UserRepository

/**
 * 유저 정보를 불러오는 유스케이스
 * @property userRepository 유저 정보가 저장된 레포지토리
 * @see UserRepository.loadUser
 */
class LoadUserUseCase(private val userRepository: UserRepository) {

    /**
     * @throws IllegalStateException 유저 정보가 저장되어 있지 않은경우
     */
    suspend operator fun invoke() : UserData {
        return userRepository.loadUser()
    }
}