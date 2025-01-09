package com.comet.letseat.user.local.repository

import com.comet.letseat.common.storage.LocalStorage
import com.comet.letseat.user.local.model.UserData
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IllegalStateException
import java.util.UUID

class PreferenceUserRepositoryTest {

    companion object {
        const val KEY = "UUID_KEY"
    }

    // 실질적인 데이터 저장소 (DAO)
    lateinit var localStorage: LocalStorage

    lateinit var repository: PreferenceUserRepository

    @Before
    fun before() {
        // init. 추후 적절하게 every 조건 필요
        localStorage = mockk()
        // repository init
        repository = PreferenceUserRepository(localStorage)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // 유저가 존재하는 경우
    @Test
    fun testUserExist() = runTest {
        coEvery { localStorage.getString(any(), any()) } returns UUID.randomUUID().toString() //어떤값이 오건 uuid str 반환
        assertTrue(repository.isUserExists())
    }

    // 유저가 존재하지 않는 경우
    @Test
    fun testUserDoesNotExist() = runTest {
        coEvery { localStorage.getString(any(), any()) } returns "" //빈 문자열 (원래는 2번째 파라미터 반환 - "null"로 지정되어있음)
        assertFalse(repository.isUserExists())
    }

    // 유저 정보 저장 테스트
    @Test
    fun testSaveUser() = runTest {
        val userData : UserData = UserData(UUID.randomUUID()) // 저장될 유저 데이터
        val captureKey : CapturingSlot<String> = CapturingSlot() // 캡처할 키
        val captureValue : CapturingSlot<String> = CapturingSlot() // 캡처할 값

        // 로컬 저장소 캡처
        coEvery { localStorage.putString(capture(captureKey), capture(captureValue)) } returns mockk()

        repository.saveUser(userData)
        coVerify(atLeast = 1) { localStorage.putString(any(), any()) } // 호출이 되었는지 확인
        assertEquals(KEY, captureKey.captured) // HARD CODED.. private 필드라 일단 이렇게
        assertEquals(userData.uuid.toString(), captureValue.captured)
    }


    // 유저 정보 로드 성공시
    @Test
    fun testLoadUserSuccess() = runTest {
        val randomUUID = UUID.randomUUID()
        coEvery { localStorage.getString(KEY, any()) } returns randomUUID.toString()

        kotlin.runCatching {
            val data = repository.loadUser()
            assertEquals(randomUUID, data.uuid)
        }.onFailure {
            fail() // does not throw 없어서 대신
        }
    }

    // 유저 정보 로드 실패시
    @Test
    fun testLoadUserFailure() = runTest {
        coEvery { localStorage.getString(KEY, any()) } returns ""
        assertThrows(IllegalStateException::class.java) { runBlocking { repository.loadUser() } }
    }
}