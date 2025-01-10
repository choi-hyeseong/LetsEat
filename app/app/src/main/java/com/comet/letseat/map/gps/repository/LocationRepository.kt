package com.comet.letseat.map.gps.repository

import com.comet.letseat.map.gps.model.UserLocation

/**
 * 유저 위치 정보를 가져오는 레포지토리
 */
interface LocationRepository {

    /**
     * 정보를 가져올 주체가 Ready 되었는지 확인
     */
    fun isEnabled() : Boolean

    /**
     * 유저 정보를 가져옵니다.
     * @return 유저 위치 정보를 반환합니다. 실패시 2가지의 Exception을 포함합니다.
     * @see IllegalStateException isEnabled가 false인경우 발생합니다.
     * @see NullPointerException gps 정보를 가져오지 못할때 발생합니다.
     */
    fun getLocation() : Result<UserLocation>
}