package com.comet.letseat.user.history.view

import android.util.Log
import com.ViewModelTest
import com.comet.letseat.getOrAwaitValue
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.type.NetworkErrorType
import com.comet.letseat.user.remote.user.dto.HistoryResponse
import com.comet.letseat.user.remote.user.model.UserHistory
import com.comet.letseat.user.remote.user.usecase.GetUserHistoryUseCase
import com.skydoves.sandwich.ApiResponse
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.lang.Thread.sleep
import java.lang.reflect.Field
import java.util.UUID
import java.util.concurrent.TimeUnit

class HistoryViewModelTest : ViewModelTest() {

    // mock용 필드
    lateinit var loadUserUseCase: LoadUserUseCase
    lateinit var getUserHistoryUseCase: GetUserHistoryUseCase

    // 초기화될 vm
    lateinit var viewModel: HistoryViewModel

    // 초기화될 private field
    lateinit var cacheUserField: Field // reflection

    @Before
    fun before() {
        // mock
        loadUserUseCase = mockk()
        getUserHistoryUseCase = mockk()

        viewModel = HistoryViewModel(loadUserUseCase, getUserHistoryUseCase)

        // field 가져오기
        cacheUserField = HistoryViewModel::class.java.getDeclaredField("cachedUser").also {
            it.isAccessible = true // 접근제한자 풀기
        }

        // log mock
        mockkStatic(Log::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
        cacheUserField.isAccessible = false // 접근 제한자 다시 설정
    }

    // 캐시가 설정되지 않은경우 load수행하는 모습 확인
    @Test
    fun testSetupUserCache() {
        val resultUUID = UUID.randomUUID() // result uuid
        // 리턴
        coEvery { loadUserUseCase() } returns UserData(resultUUID) // uuid 리턴
        // 이후 로직 간략하게 수행하기 위해 history는 exception 생기게
        coEvery { getUserHistoryUseCase(any()) } returns ApiResponse.error(IllegalStateException())
        // log error mocking
        every { Log.e(any<String>(), any<String>(), any()) } returns 0 // 에러 방지

        // history load 요청시
        viewModel.loadHistory()

        sleep(100) // IO Coroutine 실행 대기. sleep 쓰니까 코루틴이라 그런지 필드를 바로 가져올 수 없음.
        val uuidCache = cacheUserField.get(viewModel) as UUID // uuid 필드 가져옴
        assertEquals(resultUUID, uuidCache)
    }

    // 유저 이력 로드 성공시
    @Test
    fun testLoadHistorySuccess() {
        // input uuid
        val uuid = UUID.randomUUID()
        // uuid capture
        val captureUUID: CapturingSlot<UUID> = CapturingSlot()
        // expected result
        val response = HistoryResponse(mutableListOf(UserHistory(uuid, System.currentTimeMillis(), mutableListOf(), mutableListOf(), "")))
        // uuid 리턴
        coEvery { loadUserUseCase() } returns UserData(uuid) // input uuid 제공
        // history 리턴하기
        coEvery { getUserHistoryUseCase(capture(captureUUID)) } returns ApiResponse.of { Response.success(response) }

        // 호출
        viewModel.loadHistory()

        kotlin.runCatching {
            // vm 관측
            viewModel.historyLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertEquals(response.histories, it) // 응답 결과 같은지
        }.onFailure {
            fail()
        }

        // error live data로 들어오는 데이터 없는지 확인
        kotlin.runCatching {
            viewModel.errorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }
        // assertEquals를 마지막에 하는 이유 - getOrAwaitValue를 함으로써 코루틴 함수 실행 대기가 되므로.
        assertEquals(uuid, captureUUID.captured) // input 같은지
    }

    // 이력 가져오다 error가 발생한경우 - bad request ..
    @Test
    fun testLoadHistoryFailure_With_Error() {
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // 유저 정보 제공

        // 에러 결과 리턴 - 400 bad request
        val errorResponse : ApiResponse<HistoryResponse> = ApiResponse.of { Response.error(400, ResponseBody.Companion.create("application/json".toMediaType(), "")) }
        coEvery { getUserHistoryUseCase(any()) } returns errorResponse

        // log mock
        every { Log.w(any(), any<String>()) } returns 0

        // history 요청
        viewModel.loadHistory()

        // error live data 감지
        kotlin.runCatching {
            viewModel.errorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {event ->
            assertFalse(event.isHandled) // 핸들 안됨
            assertEquals(NetworkErrorType.ERROR, event.getContent())
        }.onFailure {
            fail()
        }

        // live data response 갱신 안됨
        kotlin.runCatching {
            viewModel.historyLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }
        verify(exactly = 1) { Log.w(any(), any<String>()) } // 로그 호출 확인
    }

    // 이력 가져오다 exception이 발생한경우 - IOException 등
    @Test
    fun testLoadHistoryFailure_With_Exception() {
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // 유저 정보 제공

        // 예외 결과 리턴
        val exceptionResponse : ApiResponse<HistoryResponse> = ApiResponse.error(IOException())
        coEvery { getUserHistoryUseCase(any()) } returns exceptionResponse

        // log mock
        every { Log.e(any(), any<String>(), any()) } returns 0

        // history 요청
        viewModel.loadHistory()

        // error live data 감지
        kotlin.runCatching {
            viewModel.errorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {event ->
            assertFalse(event.isHandled) // 핸들 안됨
            assertEquals(NetworkErrorType.EXCEPTION, event.getContent())
        }.onFailure {
            fail()
        }

        // live data response 갱신 안됨
        kotlin.runCatching {
            viewModel.historyLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }
        verify(exactly = 1) { Log.e(any(), any<String>(), any()) } // 로그 호출 확인
    }
}