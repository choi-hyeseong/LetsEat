package com.comet.letseat.map.gps.usecase

import com.comet.letseat.map.gps.model.UserLocation
import com.comet.letseat.map.gps.repository.LocationRepository

/**
 * 위치 정보 가져오는 유스케이스
 * @see LocationRepository.getLocation
 */
class GetLocationUseCase(private val locationRepository: LocationRepository) {

    operator fun invoke() : Result<UserLocation> {
        return locationRepository.getLocation()
    }
}