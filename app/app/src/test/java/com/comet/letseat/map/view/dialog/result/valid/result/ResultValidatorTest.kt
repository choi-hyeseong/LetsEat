package com.comet.letseat.map.view.dialog.result.valid.result

import com.comet.letseat.common.view.state.ViewCheckState
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// 최종 메뉴 선택지 다이얼로그 검증기 테스트
class ResultValidatorTest {
    lateinit var validator: ResultValidator

    @Before
    fun before() {
        validator = ResultValidator()
    }

    @After
    fun tearDown() {
        // do something
        unmockkAll()
    }


    // 검증 성공시
    @Test
    fun testValidateSuccess() {
        // 짠, 달달한 2개의 카테고리중 달달한이 true로 체크되어 있는 경우
        val input = mutableListOf(ViewCheckState("까르보나라", false), ViewCheckState("간장치킨", true))
        val result = validator.validate(input)

        assertTrue(result.isSuccess) // 성공
        assertEquals(0, result.error.size) // 에러 비어있음
    }


    // 입력값이 아무것도 선택되지 않은경우
    @Test
    fun testValidateFail_With_Empty() {
        // 까르보나라, 간장치킨중 하나도 선택 안된경우
        val input = mutableListOf(ViewCheckState("까르보나라", false), ViewCheckState("간장치킨", false))
        val result = validator.validate(input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("checks", error.fieldName) // 에러 필드명
        assertEquals(ResultValidErrorType.EMPTY, error.error) // 에러 값
    }


    // 2개 이상 고른경우
    @Test
    fun testValidateFail_With_Multiple_Select() {
        val input = mutableListOf(ViewCheckState("까르보나라", false), ViewCheckState("간장치킨", false))
        input.forEach { it.isChecked = true } // 둘다 체크해버림

        val result = validator.validate(input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("checks", error.fieldName) // 에러 필드명
        assertEquals(ResultValidErrorType.MULTIPLE, error.error) // 에러 값
    }
}