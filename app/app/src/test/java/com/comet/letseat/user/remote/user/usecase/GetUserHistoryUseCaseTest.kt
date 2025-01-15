package com.comet.letseat.user.remote.user.usecase

import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.UUID

// 레포지토리 호출 테스트 - remote userrepository - get user history
class GetUserHistoryUseCaseTest {

    lateinit var remoteUserRepository: RemoteUserRepository

    @Before
    fun before() {
        remoteUserRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // find Store 호출 확인
    @Test
    fun testCallGetStores() = runTest {
        coEvery { remoteUserRepository.getUserHistory(any()) } returns ApiResponse.of { Response.success(mockk()) }

        val useCase = GetUserHistoryUseCase(remoteUserRepository)
        useCase(UUID.randomUUID())

        coVerify(atLeast = 1) { remoteUserRepository.getUserHistory(any()) } // 호출한지 확인
    }
}