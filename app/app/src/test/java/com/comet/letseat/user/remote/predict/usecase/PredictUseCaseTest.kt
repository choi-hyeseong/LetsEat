package com.comet.letseat.user.remote.predict.usecase

import com.comet.letseat.user.local.repository.UserRepository
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.remote.predict.repository.PredictRepository
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

// predict repository 호출 테스트
class PredictUseCaseTest {

    lateinit var predictRepository: PredictRepository

    @Before
    fun before() {
        predictRepository = mockk()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testCallPredict() = runTest {
        coEvery { predictRepository.predict(any(), any()) } returns mockk()

        val predictUseCase = PredictUseCase(predictRepository)
        predictUseCase(UUID.randomUUID(), mutableListOf())

        coVerify(atLeast = 1) { predictRepository.predict(any(), any()) }
    }

}