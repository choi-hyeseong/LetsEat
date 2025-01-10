package com.comet.letseat.map.gps.model

import android.location.Location
import com.kakao.vectormap.LatLng

/**
 * 유저의 위치 정보를 담는 데이터 객체
 * @property latitude 받아온 위도 데이터
 * @property longitude 받아온 경도 데이터
 */
data class UserLocation(val latitude : Double, val longitude : Double) {

    // 카카오맵에 사용되는 위경도로 변경
    fun toLatlng() : LatLng = LatLng.from(latitude, longitude)
}

fun Location.toUserModel() : UserLocation = UserLocation(latitude, longitude)