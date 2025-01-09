package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

// 레포지토리 호출 확인 - 유저 로드
class LoadUserUseCaseTest {

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
    fun testCallLoadUser() = runTest {
        val userData = UserData(UUID.randomUUID())
        coEvery { userRepository.loadUser() } returns userData

        val loadUserUseCase = LoadUserUseCase(userRepository)
        val result = loadUserUseCase()

        coVerify(atLeast = 1) { userRepository.loadUser() }
        assertEquals(userData, result)
    }
}