package com.comet.letseat.common.view

import android.view.View
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.Thread.sleep
import java.lang.reflect.Field

// 쓰로틀링 테스트
@Suppress("UNCHECKED_CAST")
class ThrottleOnClickListenerTest {

    // 쓰로틀링 시간 필드
    lateinit var throttleField : Field
    // 함수 할당 필드
    lateinit var functionField : Field

    @Before
    fun before() {
        // reflection으로 필드 접근 허용
        throttleField = ThrottleOnClickListener::class.java.getDeclaredField("throttleMillis").also { it.isAccessible = true }
        functionField = ThrottleOnClickListener::class.java.getDeclaredField("clickFunction").also { it.isAccessible = true }
    }

    @After
    fun tearDown() {
        // accessible 끄기
        throttleField.isAccessible = false
        functionField.isAccessible = false
    }

    // 기본적으로 생성되는 밀리세컨드는 300ms인지 검증
    @Test
    fun testDefaultMillis_Equals_300() {
        // listener init
        val lambda : (View) -> Unit = { it.isEnabled = true }
        val listener = ThrottleOnClickListener(lambda)

        // 필드 그대로 가져옴
        val millis = throttleField.get(listener) as Long
        val function = functionField.get(listener) as (View) -> Unit

        assertEquals(300L, millis)
        assertEquals(lambda, function)
    }

    // 생성자 파라미터 정상적으로 들어가는지 확인
    @Test
    fun testConstructorInitialize() {
        // listener init
        val throttle : Long = 450L
        val lambda : (View) -> Unit = { it.isEnabled = true }
        val listener = ThrottleOnClickListener(throttle, lambda)

        // 필드 그대로 가져옴
        val millis = throttleField.get(listener) as Long
        val function = functionField.get(listener) as (View) -> Unit

        assertEquals(throttle, millis)
        assertEquals(lambda, function)
    }

    // 쓰로틀링 시간보다 긴 시간에 클릭해서 클릭 성공하는 경우
    @Test
    fun testOnClickSuccess() = runTest {
        var count = 0 // 리스너에서 통과한 카운트 수

        val loop = 10 // 루프할 시행수. count가 loop만큼 증가해야함
        val millis = 50L // 지연시간

        val listener = ThrottleOnClickListener(millis) { count++ } // 호출될때마다 카운트 증가
        repeat(loop) {
            listener.onClick(mockk())
            sleep(millis.plus(10)) // 조금 지연주고 테스트
        }
        assertEquals(loop, count)
    }

    // 쓰로틀링 시간보다 긴 시간에 클릭해서 클릭 성공하는 경우
    @Test
    fun testOnClickFailure() = runTest {
        var count = 0 // 리스너에서 통과한 카운트 수

        val loop = 10 // 루프할 시행수. count가 loop만큼 증가해야함
        val millis = 50L // 지연시간

        val listener = ThrottleOnClickListener(millis) { count++ } // 호출될때마다 카운트 증가
        repeat(loop) {
            listener.onClick(mockk())
            sleep(millis.minus(10)) // 지연시간보다 작은 시간 동안 sleep
        }
        assertNotEquals(loop, count)
        // count는 10은 되지 않지만, 1번째 시도에서 실패한다면 2번째 시도는 성공함. -> 아마 절반정도 클릭 될듯
    }

    //밀리세컨드가 제공되지 않은 확장함수 테스트
    @Test
    fun testNonMillsParamExtension() {
        val mockView : View = mockk() // view mock
        val captureListener : CapturingSlot<View.OnClickListener> = slot()

        every { mockView.setOnClickListener(capture(captureListener)) } returns mockk() // onclick listener capture

        val function : (View) -> Unit = { it.isEnabled = true } // 제공될 함수
        mockView.setThrottleClickListener(function)

        verify(atLeast = 1) { mockView.setOnClickListener(any()) } // onclick 적용했는지

        // 캡처된 쓰로틀 리스너
        val captured = captureListener.captured
        assertTrue(captured is ThrottleOnClickListener) // throttle listener인지 확인

        val throttleListener = captured as ThrottleOnClickListener // casting

        // 필드 그대로 가져옴
        val millis = throttleField.get(throttleListener) as Long
        val throttleFunction = functionField.get(throttleListener) as (View) -> Unit

        // 가져온 값이 설정되었는지.
        assertEquals(300L, millis)
        assertEquals(function, throttleFunction)
    }

    // 밀리세컨드가 지정된 확장함수 테스트
    @Test
    fun testMillisParamExtension() {
        val mockView: View = mockk() // view mock
        val captureListener: CapturingSlot<View.OnClickListener> = slot()

        every { mockView.setOnClickListener(capture(captureListener)) } returns mockk() // onclick listener capture

        val millis = 345L
        val function: (View) -> Unit = { it.isEnabled = true } // 제공될 함수
        mockView.setThrottleClickListener(millis, function)

        verify(atLeast = 1) { mockView.setOnClickListener(any()) } // onclick 적용했는지

        // 캡처된 쓰로틀 리스너
        val captured = captureListener.captured
        assertTrue(captured is ThrottleOnClickListener) // throttle listener인지 확인

        val throttleListener = captured as ThrottleOnClickListener // casting

        // 필드 그대로 가져옴
        val throttleMillis = throttleField.get(throttleListener) as Long
        val throttleFunction = functionField.get(throttleListener) as (View) -> Unit

        // 가져온 값이 설정되었는지.
        assertEquals(millis, throttleMillis)
        assertEquals(function, throttleFunction)
    }

}