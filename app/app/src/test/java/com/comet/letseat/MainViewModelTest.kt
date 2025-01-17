package com.comet.letseat

import com.ViewModelTest
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.ExistUserUseCase
import com.comet.letseat.user.local.usecase.SaveUserUseCase
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

// 메인 액티비티 실행시 유저 정보 체크 및 생성 테스트
class MainViewModelTest : ViewModelTest() {

    // 유저 존재 여부 유스케이스
    lateinit var existUserUseCase: ExistUserUseCase

    // 유저 정보 저장 유스케이스
    lateinit var saveUserUseCase: SaveUserUseCase

    // vm
    lateinit var viewModel: MainViewModel



    @Before
    fun before() {
        // mock
        existUserUseCase = mockk()
        saveUserUseCase = mockk()
        // 할당
        viewModel = MainViewModel(existUserUseCase, saveUserUseCase)
    }

    @After
    fun tearDown() {
        //할당 해지
        unmockkAll()
    }

    // 유저 정보가 이미 있는경우
    @Test
    fun testAlreadyHasData() = runTest {
        coEvery { existUserUseCase() } returns true //유저 정보가 있는경우

        viewModel.initUserData() // init 호출

        // 결과 관측
        kotlin.runCatching {
            // live data 관측 - 성공 여부
            viewModel.initializeLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertTrue(it)
        }.onFailure {
            fail() // assert does not throw 용도
        }
        coVerify(exactly = 0) { saveUserUseCase(any()) } // 호출 되지 않음 감지
    }

    // 유저 정보가 없어서 저장 확인
    @Test
    fun testDoesNotHaveUserData() = runTest {
        val userCapture : CapturingSlot<UserData> = CapturingSlot() // 유저 정보 입력 캡처용

        coEvery { existUserUseCase() } returns false // 유저 정보 없음
        coEvery { saveUserUseCase(capture(userCapture)) } returns Unit // 유저 정보 저장 캡처

        viewModel.initUserData() // 유저 정보 init
        // 결과 관측
        kotlin.runCatching {
            // live data 관측 - 성공 여부
            viewModel.initializeLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertTrue(it) // true 리턴

            coVerify(exactly = 1) { saveUserUseCase(any()) } // 저장여부 호출 확인
            assertTrue(userCapture.isCaptured) // 캡처 된지 확인
        }.onFailure {
            fail() // assert does not throw 용도
        }

    }
}