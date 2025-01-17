package com.comet.letseat.user.setting

import com.ViewModelTest
import com.comet.letseat.getOrAwaitValue
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.user.usecase.RemoteDeleteUserUseCase
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.TimeUnit

// 유저 정보 삭제 뷰모델 테스트
class SettingViewModelTest : ViewModelTest() {

    // 유스케이스 필드
    lateinit var loadUserUseCase: LoadUserUseCase
    lateinit var deleteUserUseCase: DeleteUserUseCase
    lateinit var remoteDeleteUserUseCase: RemoteDeleteUserUseCase
    // 뷰모델
    lateinit var viewModel: SettingViewModel

    @Before
    fun before() {
        // mock
        loadUserUseCase = mockk()
        deleteUserUseCase = mockk()
        remoteDeleteUserUseCase = mockk()
        // 할당
        viewModel = SettingViewModel(loadUserUseCase, deleteUserUseCase, remoteDeleteUserUseCase)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // 원격지 삭제 성공으로 전체 데이터 삭제 성공
    @Test
    fun testDeleteSuccess() = runTest {
        val inputUUID = UUID.randomUUID() // random input uuid
        val inputCapture : CapturingSlot<UUID> = CapturingSlot() // uuid input 캡처용

        coEvery { loadUserUseCase() } returns UserData(inputUUID) // uuid 리턴 성공
        coEvery { remoteDeleteUserUseCase(capture(inputCapture)) } returns true // 삭제 성공 및 캡처
        coEvery { deleteUserUseCase() } returns Unit // 삭제 요청 mock

        // 삭제 요청
        viewModel.deleteUser()

        kotlin.runCatching {
            viewModel.deleteResponseLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertTrue(it) // 리턴값이 true
        }.onFailure {
            fail() // timeout
        }

        // 호출 확인
        coVerify(exactly = 1) { loadUserUseCase() }
        coVerify(exactly = 1) { remoteDeleteUserUseCase(any()) }
        coVerify(exactly = 1) { deleteUserUseCase() }
        // 입력값 같은지
        assertEquals(inputUUID, inputCapture.captured)
    }

    // 원격지 삭제 실패
    @Test
    fun testDeleteFailure_With_Remote_Call_Fail() = runTest {
        // mock 설정
        coEvery { loadUserUseCase() } returns UserData(UUID.randomUUID()) // uuid 리턴 성공
        coEvery { remoteDeleteUserUseCase(any()) } returns false // 삭제 실패

        // 삭제 요청
        viewModel.deleteUser()

        kotlin.runCatching {
            viewModel.deleteResponseLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it) // 리턴값이 false
        }.onFailure {
            fail() // timeout
        }

        // 호출 확인
        coVerify(exactly = 1) { loadUserUseCase() }
        coVerify(exactly = 1) { remoteDeleteUserUseCase(any()) }
        // 호출 안됨 확인
        coVerify(exactly = 0) { deleteUserUseCase() }
    }

    // 호출될일은 로직 상 없지만, 그래도 테스트 코드는 수행
    // 메인액티비티 - 설정 순으로 거쳐 가면서 유저 정보가 초기화 되는데, 삭제 시점에는 유저 정보가 반드시 호출됨
    // 다른 앱에서 호출하지 않는다면..
    // livedata false로 나타나는거 확인
    @Test
    fun testDeleteFailure_When_Data_Not_Exists() = runTest {
        // 로드가 수행될때 에러가 생기므로 이후 로직은 작동하지 않음
        coEvery { loadUserUseCase() } throws IllegalStateException("유저 정보가 없습니다.")

        // 호출
        viewModel.deleteUser()

        // livedata false로 갱신 확인
        kotlin.runCatching {
            viewModel.deleteResponseLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it) // 에러 떴으므로 postValue로 false 리턴
        }.onFailure {
            // timeout 발생시
            fail()
        }

        // 호출 안됨
        coVerify(exactly = 0) { remoteDeleteUserUseCase(any()) }
        coVerify(exactly = 0) { deleteUserUseCase() }
    }
}