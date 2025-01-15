package com.comet.letseat.user.remote.user.usecase

import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

// 삭제 api 호출 확인
class RemoteDeleteUserUseCaseTest {

    lateinit var remoteUserRepository: RemoteUserRepository

    @Before
    fun before() {
        remoteUserRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // delete user 확인
    @Test
    fun testCallDeleteUser() = runTest {
        coEvery { remoteUserRepository.delete(any()) } returns false

        val useCase = RemoteDeleteUserUseCase(remoteUserRepository)
        useCase(UUID.randomUUID())

        coVerify(atLeast = 1) { remoteUserRepository.delete(any()) } // 호출한지 확인
    }

}