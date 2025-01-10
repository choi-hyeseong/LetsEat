package com.comet.letseat.map.gps.usecase

import com.comet.letseat.map.gps.repository.LocationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

// repository 호출 확인 - 위치 가져오기
class GetLocationUseCaseTest {

    lateinit var locationRepository: LocationRepository

    @Before
    fun before() {
        locationRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // getlocation 호출 확인
    @Test
    fun testCallGetLocation() {
        every { locationRepository.getLocation() } returns Result.success(mockk()) // 올바른 리턴

        val useCase = GetLocationUseCase(locationRepository)
        useCase()

        verify(atLeast = 1) { locationRepository.getLocation() } // 호출한지 확인
    }

}