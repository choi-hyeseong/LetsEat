package com.comet.letseat.map.kakao.repoisotry

import com.comet.letseat.map.kakao.api.KakaoAPI
import com.comet.letseat.map.kakao.dto.Document
import com.comet.letseat.map.kakao.dto.Meta
import com.comet.letseat.map.kakao.dto.SameName
import com.comet.letseat.map.kakao.dto.StoreResponseDTO
import com.skydoves.sandwich.ApiResponse
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

// 카카오맵 구현체 레포지토리 테스트
class KakaoMapRepositoryTest {

    // 초기화 될 retrofit api
    lateinit var kakaoAPI: KakaoAPI

    @Before
    fun before() {
        // 초기화
        kakaoAPI = mockk() // mock
    }

    @After
    fun tearDown() {
        unmockkAll() // 해지
    }

    // 테스트에 필요한 responseDTO 제공
    private fun provideTestResponse() : StoreResponseDTO {
        val documents = mutableListOf(Document(
            address_name = "경북 구미시",
            category_group_code = "CODE",
            category_group_name = "GROUP_NAME",
            category_name = "검색한 카테고리",
            distance = "500",
            id = "id",
            phone = "010-1234-5678",
            place_name = "스토어",
            place_url = "URL",
            road_address_name = "무슨무슨길",
            x = "117.5",
            y = "45.0"
        ))
        return StoreResponseDTO(documents, Meta(false, 0, SameName("", mutableListOf(), ""), 0))
    }

    // api 호출이 성공해서 정상적으로 모델로 변환이 이루어지는지 확인
    @Test
    fun testAPICallSuccess() = runTest {
        val queryCapture : CapturingSlot<String> = CapturingSlot()
        val xCoordCapture : CapturingSlot<String> = CapturingSlot()
        val yCoordCapture : CapturingSlot<String> = CapturingSlot()

        // param
        val query = "카카오 프렌즈"
        val x = 150.0
        val y = 130.0

        // api 호출시 성공값 반환
        coEvery { kakaoAPI.findStoresByKeyword(capture(queryCapture), capture(xCoordCapture), capture(yCoordCapture)) } returns ApiResponse.of { Response.success(provideTestResponse()) }

        // 호출
        val mapRepository = KakaoMapRepository(kakaoAPI)
        val result = mapRepository.findStoresByKeyword(query, x, y)

        // 결과 확인
        assertTrue(result.isSuccess)
        // 변환부는 dto test에서 확인 완료
        assertEquals(query, queryCapture.captured)
        assertEquals(x.toString(), xCoordCapture.captured)
        assertEquals(y.toString(), yCoordCapture.captured)

    }

    @Test
    fun testAPICallFailure() = runTest {

        // api 호출 실패
        coEvery { kakaoAPI.findStoresByKeyword(any(), any(), any()) } returns ApiResponse.error(IllegalStateException())

        val mapRepository = KakaoMapRepository(kakaoAPI)
        val result = mapRepository.findStoresByKeyword("", 0.0, 0.0)
        assertFalse(result.isSuccess) // 실패
    }


}