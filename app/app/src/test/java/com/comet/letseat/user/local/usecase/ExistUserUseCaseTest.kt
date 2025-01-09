package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// 레포지토리 호출 확인 - 유저 존재 확인
class ExistUserUseCaseTest {

    lateinit var userRepository: UserRepository

    @Before
    fun before() {
        userRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testCallExistUser() = runTest {
        val isUserExist = true
        coEvery { userRepository.isUserExists() } returns isUserExist

        val existUserUseCase = ExistUserUseCase(userRepository)
        val result = existUserUseCase()

        coVerify(atLeast = 1) { userRepository.isUserExists() }
        assertTrue(result)
    }
}