package com.comet.letseat.map.kakao.usecase

import com.comet.letseat.map.kakao.repoisotry.MapRepository
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// 유스케이스 레포지토리 호출 테스트
class GetStoresByKeywordUseCaseTest {

    lateinit var mapRepository: MapRepository

    @Before
    fun before() {
        mapRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // find Store 호출 확인 및 전달된 값이 올바른지 확인
    @Test
    fun testCallGetStores() = runTest {
        // 값 캡처용
        val queryCapture : CapturingSlot<String> = CapturingSlot()
        val xCapture : CapturingSlot<Double> = CapturingSlot()
        val yCapture : CapturingSlot<Double> = CapturingSlot()
        // input
        val query = "카카오맵"
        val x = 15.5
        val y = 17.7

        coEvery { mapRepository.findStoresByKeyword(capture(queryCapture), capture(xCapture), capture(yCapture)) } returns Result.success(mockk()) // 올바른 리턴

        val useCase = GetStoresByKeywordUseCase(mapRepository)
        useCase(query, x,y)

        coVerify(atLeast = 1) { mapRepository.findStoresByKeyword(any(), any(), any()) } // 호출한지 확인
        assertEquals(query, queryCapture.captured)
        assertEquals(x, xCapture.captured, 0.0)
        assertEquals(y, yCapture.captured, 0.0)
    }
}