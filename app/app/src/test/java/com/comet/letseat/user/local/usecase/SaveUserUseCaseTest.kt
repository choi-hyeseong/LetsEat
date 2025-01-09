package com.comet.letseat.user.local.usecase

import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.repository.UserRepository
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

// 레포지토리 호출 확인 - 유저 저장
class SaveUserUseCaseTest {

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
        val userCapture : CapturingSlot<UserData> = CapturingSlot()
        coEvery { userRepository.saveUser(capture(userCapture)) } returns mockk()

        val saveUserUseCase = SaveUserUseCase(userRepository)
        saveUserUseCase(userData)

        coVerify(atLeast = 1) { userRepository.saveUser(any()) }
        assertEquals(userData, userCapture.captured)
    }
}