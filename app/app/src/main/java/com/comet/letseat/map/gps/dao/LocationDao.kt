package com.comet.letseat.map.gps.dao

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

/**
 * GPS 정보에 접근하는 DAO. 현재는 그냥 사용했으나 인터페이스 분리해도 괜찮을듯.
 *
 * 지속적인 위치 트래킹은 필요하지 않을듯하여 여기선 Listener 미사용
 * @property context application Context - 서비스 접근용
 * @property locationManager context로 가져오는 gps 서비스입니다.
 */
class LocationDao(private val context : Context) {

    companion object {
        // network를 이용해서 위치를 가져올 provider 입니다.
        private const val PROVIDER : String = LocationManager.NETWORK_PROVIDER
    }
    private val locationManager : LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    /**
     * GPS가 활성화 되어 있는지 확인 (여기서는 네트워크 제공자 사용)
     */
    fun isEnabled() : Boolean {
        return locationManager.isProviderEnabled(PROVIDER) // S버전 이상부터는 혼합형 (Fused) 사용가능해서 네트워크랑 gps 혼합가능
    }

    /**
     * 좌표 정보 가져오기. activity에서 permission 체크후 사용할것
     * @throws IllegalStateException GPS가 활성화 되어 있지 않은경우 호출됩니다.
     */
    @SuppressLint("MissingPermission")
    fun getLocation() : Location? {
        if (!isEnabled())
            throw IllegalStateException("GPS가 활성화 되어 있지 않습니다.")
        return locationManager.getLastKnownLocation(PROVIDER)
    }


}