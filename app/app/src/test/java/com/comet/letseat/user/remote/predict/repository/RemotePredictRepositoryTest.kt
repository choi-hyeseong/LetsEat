package com.comet.letseat.user.remote.predict.repository

import com.comet.letseat.user.remote.predict.PredictAPI
import com.comet.letseat.user.remote.predict.dto.PredictRequest
import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.getOrNull
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

// predict repository test
// 사실상 api call 그대로 반환하는거라 그냥 리턴값 비교만 적절히 하면 될듯
class RemotePredictRepositoryTest {

    lateinit var predictAPI: PredictAPI

    @Before
    fun before() {
        predictAPI = mockk() // mocking
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // api 호출 성공 값 받을시 및 값 캡쳐
    @Test
    fun testAPICallSuccess_With_Capture() = runTest {
        // 입력값
        val uuid : UUID = UUID.randomUUID()
        val categories : List<String> = mutableListOf("밥", "라면", "새우")

        // 출력값
        val menus = mutableListOf<String>("까르보나라", "투움바")
        val response : ApiResponse<PredictResponse> = ApiResponse.of { Response.success(PredictResponse(menus)) } //일단 성공

        val requestCapture : CapturingSlot<PredictRequest> = CapturingSlot() // request input 캡처용

        // 캡처
        coEvery { predictAPI.predict(capture(requestCapture)) } returns response

        // 호출
        val repository = RemotePredictRepository(predictAPI)
        val result = repository.predict(uuid, categories)

        assertNotNull(result.getOrNull()) // success
        assertEquals(menus, result.getOrNull()?.menus)

        // 캡처된 값 확인
        val capture = requestCapture.captured
        assertEquals(uuid, capture.uuid)
        assertEquals(categories, capture.categories)


    }

    // api 호출 실패시
    @Test
    fun testAPICallFailure() = runTest {
        // 입력값
        val response : ApiResponse<PredictResponse> = ApiResponse.of { Response.error(400, mockk()) } //일단 성공

        coEvery { predictAPI.predict(any()) } returns response

        // 호출
        val repository = RemotePredictRepository(predictAPI)
        val result = repository.predict(UUID.randomUUID(), mutableListOf())

        assertNull(result.getOrNull()) // success
    }
}