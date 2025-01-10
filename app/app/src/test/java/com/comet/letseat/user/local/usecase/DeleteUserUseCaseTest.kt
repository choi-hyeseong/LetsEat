package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// 레포지토리 호출 테스트 - 삭제
class DeleteUserUseCaseTest {

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
    fun testCallDeleteUser() = runTest {
        coEvery { userRepository.delete() } returns mockk()

        val deleteUserUseCase = DeleteUserUseCase(userRepository)
        deleteUserUseCase()

        coVerify(atLeast = 1) { userRepository.delete() }
    }

}