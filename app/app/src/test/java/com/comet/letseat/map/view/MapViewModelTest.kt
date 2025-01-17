package com.comet.letseat.map.view

import android.util.Log
import com.ViewModelTest
import com.comet.letseat.getOrAwaitValue
import com.comet.letseat.map.gps.model.UserLocation
import com.comet.letseat.map.gps.usecase.GetLocationUseCase
import com.comet.letseat.map.gps.usecase.GpsEnabledUseCase
import com.comet.letseat.map.kakao.model.Store
import com.comet.letseat.map.kakao.usecase.GetStoresByKeywordUseCase
import com.comet.letseat.map.view.type.GPSErrorType
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.comet.letseat.user.remote.predict.usecase.PredictUseCase
import com.comet.letseat.user.remote.type.NetworkErrorType
import com.skydoves.sandwich.ApiResponse
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.lang.Thread.sleep
import java.lang.reflect.Field
import java.util.UUID
import java.util.concurrent.TimeUnit

// 맵 뷰모델 테스트
class MapViewModelTest : ViewModelTest() {

    // viewmodel 필드
    lateinit var gpsEnabledUseCase: GpsEnabledUseCase
    lateinit var getLocationUseCase: GetLocationUseCase
    lateinit var loadUserUseCase: LoadUserUseCase
    lateinit var predictUseCase: PredictUseCase
    lateinit var getStoresByKeywordUseCase: GetStoresByKeywordUseCase

    // viewmodel
    lateinit var viewModel: MapViewModel

    // 초기화될 private field
    lateinit var cacheUserField: Field // reflection

    @Before
    fun before() {
        // mock
        gpsEnabledUseCase = mockk()
        getLocationUseCase = mockk()
        loadUserUseCase = mockk()
        predictUseCase = mockk()
        getStoresByKeywordUseCase = mockk()
        // vm init
        viewModel = MapViewModel(gpsEnabledUseCase, getLocationUseCase, loadUserUseCase, predictUseCase, getStoresByKeywordUseCase)
        // field 가져오기
        cacheUserField = MapViewModel::class.java.getDeclaredField("cachedUser").also {
            it.isAccessible = true // 접근제한자 풀기
        }
        mockkStatic(Log::class) // log mock
    }

    @After
    fun tearDown() {
        unmockkAll()
        cacheUserField.isAccessible = false // 접근제한자 복구
    }

    // gps 비활성화로 인해 작동하지 못하는경우
    @Test
    fun testLoadLocationFailure_With_GPS_Disabled() {
        every { gpsEnabledUseCase() } returns false //gps 비활성화 일때
        // 로드 요청
        viewModel.loadLocation()
        // 에러 감지
        kotlin.runCatching {
            viewModel.gpsResponseErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            // 에러 가져온경우
            assertFalse(it.isHandled)
            assertEquals(GPSErrorType.NOT_ENABLED, it.getContent())
        }.onFailure {
            // 대기 실패시
            fail()
        }
        // 호출 안됨
        verify(exactly = 0) { getLocationUseCase() }
    }

    // 유저 위치 정보 로드 성공시
    @Test
    fun testLoadLocationSuccess() {
        val userLocation = UserLocation(15.5, 17.7)
        every { gpsEnabledUseCase() } returns true // gps 활성화
        every { getLocationUseCase() } returns Result.success(userLocation) // 유저 위치 정보 로드

        // 로드 요청
        viewModel.loadLocation()

        // 유저 정보 로드 확인
        kotlin.runCatching {
            viewModel.locationLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertEquals(userLocation, it)
        }.onFailure {
            fail()
        }


        // 에러 없음 보증
        kotlin.runCatching {
            viewModel.gpsResponseErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }
        // 호출 됨 보증
        verify(exactly = 1) { getLocationUseCase() }
    }

    // 유저 위치 정보 로드 과정에서 실패시 - GPS 비활성화
    // 위에서도 이미 체크해서 도달하지는 않지만, 로드 과정에서 실패한경우 핸들링 위해서 추가
    @Test
    fun testLoadLocationFailure_With_Disabled() {
        every { gpsEnabledUseCase() } returns true // gps 활성화
        every { getLocationUseCase() } returns Result.failure(IllegalStateException("GPS DISABLED"))

        // 로드 요청
        viewModel.loadLocation()


        // 유저 정보 로드 실패 보증
        kotlin.runCatching {
            viewModel.locationLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        // GPS 비활성화 오류
        kotlin.runCatching {
            viewModel.gpsResponseErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled)
            assertEquals(GPSErrorType.NOT_ENABLED, it.getContent())
        }.onFailure {
            fail()
        }

        // 호출 됨 보증
        verify(exactly = 1) { getLocationUseCase() }
    }

    // 유저 위치 정보 로드 과정에서 실패시 - GPS 로드 오류
    @Test
    fun testLoadLocationFailure_With_Error() {
        every { gpsEnabledUseCase() } returns true // gps 활성화
        every { getLocationUseCase() } returns Result.failure(NullPointerException("NULL"))

        // 로드 요청
        viewModel.loadLocation()


        // 유저 정보 로드 실패 보증
        kotlin.runCatching {
            viewModel.locationLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        // GPS 로드 실패
        kotlin.runCatching {
            viewModel.gpsResponseErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled)
            assertEquals(GPSErrorType.LOAD_FAIL, it.getContent())
        }.onFailure {
            fail()
        }

        // 호출 됨 보증
        verify(exactly = 1) { getLocationUseCase() }
    }

    // 유저 위치 정보 로드 과정에서 실패시 - GPS 권한 문제
    @Test
    fun testLoadLocationFailure_With_Permission() {
        every { gpsEnabledUseCase() } returns true // gps 활성화
        every { getLocationUseCase() } returns Result.failure(SecurityException("펄미션 부족"))

        // 로드 요청
        viewModel.loadLocation()


        // 유저 정보 로드 실패 보증
        kotlin.runCatching {
            viewModel.locationLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        // GPS 펄미션 오류 감지
        kotlin.runCatching {
            viewModel.gpsResponseErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled)
            assertEquals(GPSErrorType.INTERNAL, it.getContent())
        }.onFailure {
            fail()
        }

        // 호출 됨 보증
        verify(exactly = 1) { getLocationUseCase() }
    }

    // 캐시가 설정되지 않은경우 load수행하는 모습 확인
    @Test
    fun testSetupUserCache() {
        val resultUUID = UUID.randomUUID() // result uuid
        // 리턴
        coEvery { loadUserUseCase() } returns UserData(resultUUID) // uuid 리턴
        // 이후 로직 간략하게 수행하기 위해 predict는 exception 생기게
        coEvery { predictUseCase(any(), any()) } returns ApiResponse.error(IllegalStateException())
        // log error mocking
        every { Log.e(any<String>(), any<String>(), any()) } returns 0 // 에러 방지

        // predict 요청시
        viewModel.predict(mutableListOf())

        Thread.sleep(100) // IO Coroutine 실행 대기. sleep 쓰니까 코루틴이라 그런지 필드를 바로 가져올 수 없음.
        val uuidCache = cacheUserField.get(viewModel) as UserData // uuid 필드 가져옴
        assertEquals(resultUUID, uuidCache.uuid)
    }

    // predict 요청시 로딩 시작과 종료 확인
    @Test
    fun testPredictLoading() {
        val delay = 500L // 지연시간
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // uuid 리턴
        // 이후 로직 간략하게 수행하기 위해 history는 exception 생기게.
        // 이때 livedata loading 부분 관측 위해 answer로 지연 생기게
        coEvery { predictUseCase(any(), any()) } coAnswers {
            delay(delay)
            ApiResponse.error(IllegalStateException())
        }
        // log error mocking
        every { Log.e(any<String>(), any<String>(), any()) } returns 0 // 에러 방지

        // predict 요청시
        viewModel.predict(mutableListOf())

        kotlin.runCatching {
            viewModel.loadingLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertTrue(it.getContent()!!) // true여야 함
        }.onFailure {
            fail()
        }

        sleep(delay + 100) // 서버 api 요청이 끝날때까지 지연
        // 이후 들어오는 로딩 종료 요청
        kotlin.runCatching {
            viewModel.loadingLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertFalse(it.getContent()!!) // false여야 함
        }.onFailure {
            fail()
        }
    }

    // predict 성공시
    @Test
    fun testPredictSuccess() {
        val uuid = UUID.randomUUID() // 입력될 uuid
        val categories: List<String> = mutableListOf("밥", "면") // 입력될 카테고리
        // response 제공
        val response = PredictResponse(mutableListOf("밥", "과자"))

        val categoryCapture: CapturingSlot<List<String>> = CapturingSlot() // 캡처용
        val uuidCapture: CapturingSlot<UUID> = CapturingSlot() // 캡처용
        coEvery { loadUserUseCase() } returns UserData(uuid) // uuid 제공

        // 입력 캡처 및 반환
        coEvery { predictUseCase(capture(uuidCapture), capture(categoryCapture)) } returns ApiResponse.of { Response.success(response) }

        viewModel.predict(categories)

        // 결과 캡처
        kotlin.runCatching {
            viewModel.predictLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertEquals(response.menus, it.getContent())
        }.onFailure {
            fail() // 로드 실패시
        }

        // 에러 전파 안됨
        kotlin.runCatching {
            viewModel.predictNetworkErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

    }

    // predict 실패시 - error
    @Test
    fun testPredictFailure_With_Error() {
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // uuid 제공

        // 요청시 에러 - 400 bad request
        coEvery { predictUseCase(any(),any()) } returns ApiResponse.of { Response.error(400, ResponseBody.Companion.create("application/json".toMediaType(), "")) }

        // log error mocking
        every { Log.e(any<String>(), any<String>()) } returns 0

        viewModel.predict(mutableListOf())

        // 결과 캡처
        kotlin.runCatching {
            viewModel.predictNetworkErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertEquals(NetworkErrorType.ERROR, it.getContent())
        }.onFailure {
            fail() // 로드 실패시
        }

        // 결과 전파 안됨
        kotlin.runCatching {
            viewModel.predictLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        coVerify(exactly = 1) { predictUseCase(any(), any()) } // predict 호출 확인
        verify(exactly = 1) { Log.e(any(), any<String>()) } // 로깅 확인
    }


    // predict 실패시 - exception
    @Test
    fun testPredictFailure_With_Exception() {
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // uuid 제공

        // 요청시 exception - IOException
        coEvery { predictUseCase(any(),any()) } returns ApiResponse.error(IOException())

        // log error mocking
        every { Log.e(any<String>(), any<String>(), any()) } returns 0

        viewModel.predict(mutableListOf())

        // 결과 캡처
        kotlin.runCatching {
            viewModel.predictNetworkErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertEquals(NetworkErrorType.EXCEPTION, it.getContent())
        }.onFailure {
            fail() // 로드 실패시
        }

        // 결과 전파 안됨
        kotlin.runCatching {
            viewModel.predictLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        coVerify(exactly = 1) { predictUseCase(any(), any()) } // predict 호출 확인
        verify(exactly = 1) { Log.e(any(), any<String>(), any()) } // 로깅 확인
    }

    // 가게 찾기 성공시
    @Test
    fun testFindStoreSuccess() {
        // input
        val x = 15.5
        val y = 17.7
        val keyword = "라면"

        // 응답 결과
        val result = mutableListOf(Store(
            name = "아부찌",
            phone = "010-1234-5678",
            address = "경북 구미시",
            latitude = 123.3,
            longitude = 456.7,
            distance = 500.0

        ))

        // 캡처
        val xCapture : CapturingSlot<Double> = CapturingSlot()
        val yCapture : CapturingSlot<Double> = CapturingSlot()
        val keywordCapture : CapturingSlot<String> = CapturingSlot()

        // 응답 결과
        coEvery { getStoresByKeywordUseCase(capture(keywordCapture), capture(xCapture), capture(yCapture)) } returns Result.success(result)

        // 요청
        viewModel.findStores(x, y, keyword)

        // livedata 갱신 확인
        kotlin.runCatching {
            viewModel.storeLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertEquals(result, it)
        }.onFailure {
            fail() // 로드 실패시
        }

        // 에러 전파 안됨 확인
        kotlin.runCatching {
            viewModel.storeNetworkErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail() // 에러 떠선 안됨
        }

        // 캡처된 값 확인
        assertEquals(x, xCapture.captured, 0.0)
        assertEquals(y, yCapture.captured, 0.0)
        assertEquals(keyword, keywordCapture.captured)
    }

    // 가게 찾기 실패시
    @Test
    fun testFindStoreFailure() {
        // 응답 결과 - 요청 실패
        coEvery { getStoresByKeywordUseCase(any(), any(), any()) } returns Result.failure(IOException("에러"))
        // log error mocking
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
        // 요청
        viewModel.findStores(12.0, 45.0, "부대찌개")

        // livedata 갱신 안됨 확인
        kotlin.runCatching {
            viewModel.storeLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail() // 로드 실패시
        }

        // 에러 전파 확인
        kotlin.runCatching {
            viewModel.storeNetworkErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled) // 핸들 안됨
            assertEquals(NetworkErrorType.ERROR, it.getContent())
        }.onFailure {
            fail() // 에러 떠선 안됨
        }
        // 로깅 호출 확인
        verify(exactly = 1) { Log.e(any(), any<String>(), any()) }

    }
}

