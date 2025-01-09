package com.comet.letseat.user.local.repository

import com.comet.letseat.user.local.model.UserData

/**
 * 로컬에 기록될 유저 정보 레포지토리
 */
interface UserRepository {

    /**
     * 유저 정보가 저장되어 있는지 확인합니다.
     */
    suspend fun isUserExists() : Boolean

    /**
     * 유저 정보를 불러옵니다.
     * @throws IllegalStateException 유저 정보가 저장되어 있지 않은경우 발생합니다. isUserExist를 사용해주세요.
     */
    suspend fun loadUser() : UserData

    /**
     * 유저 정보를 저장합니다.
     * @param user 저장할 유저 정보입니다.
     */
    suspend fun saveUser(user : UserData)
}