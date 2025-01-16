package com.comet.letseat.user.remote.user.repository

import com.comet.letseat.user.remote.user.api.UserAPI
import com.comet.letseat.user.remote.user.dto.DeleteResponse
import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.dto.UserRequest
import com.comet.letseat.user.remote.user.model.UserHistory
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.getOrNull
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

// 원격 유저 정보 관리 레포지토리 테스트
class RetrofitRemoteUserRepositoryTest {

    // 유저 정보 API
    lateinit var userAPI: UserAPI

    @Before
    fun before() {
        userAPI = mockk() // 모의 객체 주입
    }

    // 해지
    @After
    fun tearDown() {
        unmockkAll()
    }

    // 유저 삭제 성공시
    @Test
    fun testDeleteUserSuccess() = runTest {
        //파라미터 검증
        val uuid = UUID.randomUUID() // 입력될 uuid
        val uuidCapture : CapturingSlot<UserRequest> = CapturingSlot()

        val response = DeleteResponse(true) //성공한 응답 결과 반환
        coEvery { userAPI.delete(capture(uuidCapture)) } returns ApiResponse.of { Response.success(response) } // 캡처 및 성공 결과 반환

        // 호출
        val repository = RetrofitRemoteUserRepository(userAPI)
        val result = repository.delete(uuid)
        // 검증
        assertTrue(result) // 성공
        assertEquals(uuid, uuidCapture.captured.uuid)  // 캡처된 파라미터 동일여부
    }

    // retrofit 호출 오류로 인해 getOrNull에서 null이 반환된경우
    @Test
    fun testDeleteUserFailure_With_Retrofit_Error() = runTest {
        coEvery { userAPI.delete(any()) } returns  ApiResponse.error(IllegalStateException()) // exception을 던지므로 getOrNull은 null이 나옴

        val repository = RetrofitRemoteUserRepository(userAPI)
        val result = repository.delete(UUID.randomUUID())
        assertFalse(result) // null이므로 false
    }

    // 응답 결과가 실패인경우
    @Test
    fun testDeleteUserFailure_With_Response() = runTest {
        val response = DeleteResponse(false) // 실패 결과값
        coEvery { userAPI.delete(any()) } returns  ApiResponse.of { Response.success(response) } // 요청은 성공했으나 결과값이 실패

        // 요청
        val repository = RetrofitRemoteUserRepository(userAPI)
        val result = repository.delete(UUID.randomUUID())
        assertFalse(result) // false를 반환하므로 실패
    }

    // 유저 검색 이력을 가져오는데 성공한경우
    @Test
    fun testLoadHistorySuccess() = runTest {
        val uuid = UUID.randomUUID() // input uuid
        val queryCapture : CapturingSlot<UserRequest> = CapturingSlot() // 쿼리 캡처

        // 예상되는 반환값
        val expectedResponse = mutableListOf(UserHistory(
            uuid,
            System.currentTimeMillis(),
            mutableListOf(),
            mutableListOf(),
            ""
        ))

        coEvery { userAPI.getHistory(capture(queryCapture)) } returns ApiResponse.of { Response.success(HistoryResponse(expectedResponse)) }

        // 요청
        val repository = RetrofitRemoteUserRepository(userAPI)
        val response = repository.getUserHistory(uuid)

        val body = response.getOrNull()
        assertNotNull(body) // 성공인지 확인
        assertEquals(1, body!!.histories.size) //내부 값 전달 됐는지 확인
        assertEquals(uuid, queryCapture.captured.uuid) // 캡처된 uuid 일치 확인
    }



    // 유저 검색 이력을 가져오는데 실패한경우
    @Test
    fun testLoadHistoryFailure() = runTest {
        coEvery { userAPI.getHistory(any()) } returns ApiResponse.error(IllegalStateException()) // retrofit error로 인해 오류가 발생한경우

        // 요청
        val repository = RetrofitRemoteUserRepository(userAPI)
        val response = repository.getUserHistory(UUID.randomUUID())

        val body = response.getOrNull()
        assertNull(body) // 실패 확인
    }


}