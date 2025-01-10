package com.comet.letseat.map.gps.usecase

import com.comet.letseat.map.gps.repository.LocationRepository

/**
 * GPS 활성화 여부 확인 유스케이스
 * @see LocationRepository.isEnabled
 */
class GpsEnabledUseCase(private val locationRepository: LocationRepository) {

    operator fun invoke() : Boolean {
        return locationRepository.isEnabled()
    }
}