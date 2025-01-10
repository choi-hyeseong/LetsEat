package com.comet.letseat.map.gps.dao

import android.content.Context
import android.location.Location
import android.location.LocationManager
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationDaoTest {

    lateinit var context : Context // mocking될 context
    lateinit var locationManager: LocationManager // gps 다루는 서비스

    companion object {
        // 현재 사용중인 provider - 네트워크 gps 제공자
        const val PROVIDER : String = LocationManager.NETWORK_PROVIDER
    }

    @Before
    fun before() {
        context = mockk() // context mocking
        locationManager = mockk() // location service mock
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // GPS가 켜져있는 경우
    @Test
    fun testGPSEnabled() = runTest {
        val providerCapture : CapturingSlot<String> = CapturingSlot() // provider 맞는지 확인

        every { locationManager.isProviderEnabled(capture(providerCapture)) } returns true // 활성화 됨
        val dao = LocationDao(context)
        assertTrue(dao.isEnabled()) // 켜져있음
        assertEquals(PROVIDER, providerCapture.captured) // 제공자 동일

    }

    // GPS가 꺼져있는 경우
    @Test
    fun testGPSDisabled() = runTest {
        val providerCapture : CapturingSlot<String> = CapturingSlot() // provider 맞는지 확인

        every { locationManager.isProviderEnabled(capture(providerCapture)) } returns false // 활성화 됨
        val dao = LocationDao(context)
        assertFalse(dao.isEnabled()) // 꺼져있음
        assertEquals(PROVIDER, providerCapture.captured) // 제공자 동일
    }

    // 위치 정보를 정상적으로 가져오는 경우
    @Test
    fun testGetLocationSuccess() = runTest {
        val location : Location = mockk() // 리턴될 객체
        val providerCapture : CapturingSlot<String> = CapturingSlot() // provider 맞는지 확인

        every { locationManager.isProviderEnabled(any()) } returns true // GPS가 활성화 됨
        every { locationManager.getLastKnownLocation(capture(providerCapture)) } returns location // provider 캡처 및 리턴

        // 가져오기
        val dao = LocationDao(context)
        val result = dao.getLocation()

        assertNotNull(result) // notnull이여야함
        assertEquals(PROVIDER, providerCapture.captured) // 제공자 동일
        assertEquals(location, result) // 결과값 일치
    }

    // 위치 정보를 가져오지 못하는 경우 - gps 꺼져있음
    @Test
    fun testGetLocationFailureWithNotEnabled() = runTest {
        every { locationManager.isProviderEnabled(any()) } returns false // GPS가 비활성화 됨

        // 가져오기
        val dao = LocationDao(context)
        assertThrows(IllegalStateException::class.java) { dao.getLocation() } // illegal state exception 발생
    }


    // 위치 정보를 가져오지 못하는 경우 - location이 null
    @Test
    fun testGetLocationFailureWithLocationNull() = runTest {
        every { locationManager.isProviderEnabled(any()) } returns true // gps가 켜져있음
        every { locationManager.getLastKnownLocation(any()) } returns null

        // 가져오기
        val dao = LocationDao(context)
        val result = dao.getLocation()
        assertNull(result)
    }



}