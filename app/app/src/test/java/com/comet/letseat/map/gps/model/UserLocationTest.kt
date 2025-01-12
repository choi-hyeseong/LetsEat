package com.comet.letseat.map.gps.model

import android.location.Location
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Test

class UserLocationTest {

    // Location을 model인 UserLocation으로 바꾸는 메소드(확장함수) 테스트
    @Test
    fun testConvertLocationToModel() {
        // return setup
        val latitude : Double = 10.0
        val longitude : Double = 20.0

        // object will return latlng
        val location : Location = mockk()
        every { location.longitude } returns longitude
        every { location.latitude } returns latitude

        val result = location.toUserModel()
        assertEquals(latitude, result.latitude)
        assertEquals(longitude, result.longitude)
    }

    // UserLocation을 KakaoMap LatLng으로 바꾸는 메소드 테스트
    @Test
    fun testConvertUserLocationToLatLng() {
        val location : UserLocation = UserLocation(15.0, 17.0)
        val result = location.toLatlng()

        assertEquals(location.latitude, result.latitude)
        assertEquals(location.longitude, result.longitude)
    }

}