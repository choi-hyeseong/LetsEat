package com.comet.letseat.map.gps.repository

import com.comet.letseat.map.gps.dao.LocationDao
import com.comet.letseat.map.gps.model.UserLocation
import com.comet.letseat.map.gps.model.toUserModel

/**
 * 네트워크 정보를 이용해서 위치를 가져오는 레포지토리 입니다.
 * @property locationDao 위치를 가져올 dao 입니다.
 */
class NetworkLocationRepository(private val locationDao: LocationDao) : LocationRepository {

    override fun isEnabled(): Boolean {
        return locationDao.isEnabled()
    }

    override fun getLocation(): Result<UserLocation> {
        return kotlin.runCatching {
            val result = locationDao.getLocation() ?: throw NullPointerException("위치 정보를 가져올 수 없습니다.")
            result.toUserModel()
        }
    }

}