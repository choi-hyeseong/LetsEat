package com.comet.letseat.common.storage

/**
 * 로컬에 저장되는 데이터를 갖고 있는 스토리지
 */
interface LocalStorage {

    /**
     * 해당 데이터를 제거합니다.
     * @param key 제거할 데이터의 키값입니다.
     */
    suspend fun delete(key: String)

    /**
     * 숫자값을 저장합니다.
     * @param key 저장할 값의 키입니다.
     * @param value 저장할 값입니다.
     */
    suspend fun putInt(key: String, value: Int)

    /**
     * 숫자값을 가져옵니다.
     * @param key 가져올 값의 키입니다.
     * @param defaultValue 해당 키값에 저장된 값이 없는경우 대신 가져올 값입니다.
     */
    suspend fun getInt(key: String, defaultValue: Int): Int

    /**
     * 문자열값을 저장합니다.
     * @param key 저장할 값의 키입니다.
     * @param value 저장할 값입니다.
     */
    suspend fun putString(key: String, value: String)

    /**
     * 문자열 값을 가져옵니다.
     * @param key 가져올 값의 키입니다.
     * @param defaultValue 해당 키값에 저장된 값이 없는경우 대신 가져올 값입니다.
     */
    suspend fun getString(key: String, defaultValue: String): String

    /**
     * 논리형값을 저장합니다.
     * @param key 저장할 값의 키입니다.
     * @param value 저장할 값입니다.
     */
    suspend fun putBoolean(key : String, value : Boolean)

    /**
     * 논리형 값을 가져옵니다.
     * @param key 가져올 값의 키입니다.
     * @param defaultValue 해당 키값에 저장된 값이 없는경우 대신 가져올 값입니다.
     */
    suspend fun getBoolean(key : String, defaultValue: Boolean) : Boolean



    /**
     * 해당 키값에 저장된 값이 있는지 확인합니다.
     * @param key 검사할 키값입니다.
     */
    suspend fun hasKey(key: String): Boolean


}