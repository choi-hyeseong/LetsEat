package com.comet.letseat.map.gps.repository

import android.location.Location
import com.comet.letseat.map.gps.dao.LocationDao
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkLocationRepositoryTest {

    lateinit var dao : LocationDao // mocking dao

    @Before
    fun before() {
        dao = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // location dao의 isEnabled 호출정도 확인
    @Test
    fun testCallLocationDaoIsEnabled() = runTest {
        every { dao.isEnabled() } returns true // enable true 리턴

        val repository = NetworkLocationRepository(dao)
        assertTrue(repository.isEnabled())
    }

    // get location 성공시
    @Test
    fun testGetLocationSuccess() = runTest {
        // return setup
        val lat : Double = 15.5
        val lng : Double = 13.3
        val location : Location = mockk()

        // will return
        every { location.latitude } returns lat
        every { location.longitude } returns lng

        every { dao.getLocation() } returns location // location 정상적으로 리턴

        val repository = NetworkLocationRepository(dao)
        val result = repository.getLocation() // result 가져옴
        val model = result.getOrNull() // 결과값
        assertNotNull(model) // notnull (success)
        assertEquals(lat, model?.latitude)
        assertEquals(lng, model?.longitude)

    }

    // get location 실패 - isEnabled되어 있지 않아 IllegalState 발생
    @Test
    fun testGetLocationFailureWithIS() = runTest {
        every { dao.getLocation() } throws IllegalStateException() // isEnabled 안되어 있는 경우

        val repository = NetworkLocationRepository(dao)
        val result = repository.getLocation() // result 가져옴
        assertFalse(result.isSuccess) // 실패
        assertEquals(IllegalStateException::class.java, result.exceptionOrNull()?.javaClass)

    }

    // get location 실패 - location이 null이여서 NPE 발생
    @Test
    fun testGetLocationFailureWithNPE() = runTest {
        every { dao.getLocation() } returns null // 위치 정보 못가져옴

        val repository = NetworkLocationRepository(dao)
        val result = repository.getLocation() // result 가져옴
        assertFalse(result.isSuccess) // 실패
        assertEquals(NullPointerException::class.java, result.exceptionOrNull()?.javaClass)

    }


}