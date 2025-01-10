package com.comet.letseat.user.local.repository

import com.comet.letseat.common.storage.LocalStorage
import com.comet.letseat.common.util.toUUID
import com.comet.letseat.user.local.model.UserData

/**
 * Preference(LocalStorage)를 이용한 유저 정보 저장소입니다.
 * @property localStorage 정보가 저장될 저장소입니다.
 */
class PreferenceUserRepository(private val localStorage: LocalStorage) : UserRepository {

    companion object {
        // UUID key
        private const val UUID_KEY : String = "UUID_KEY"
    }

    override suspend fun isUserExists(): Boolean {
        return kotlin.runCatching { loadUser() }.isSuccess
    }

    override suspend fun loadUser(): UserData {
        val data = localStorage.getString(UUID_KEY, "null")
        val uuid = data.toUUID() ?: throw IllegalStateException("데이터가 없습니다.")
        return UserData(uuid)
    }

    override suspend fun saveUser(user: UserData) {
        localStorage.putString(UUID_KEY, user.uuid.toString())
    }

    override suspend fun delete() {
        localStorage.delete(UUID_KEY)
    }
}