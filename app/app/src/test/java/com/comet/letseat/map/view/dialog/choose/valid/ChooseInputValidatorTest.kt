package com.comet.letseat.map.view.dialog.choose.valid

import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// 카테고리 입력 검증기 테스트
class ChooseInputValidatorTest {

    lateinit var validator : ChooseInputValidator
    lateinit var chooseInputs : MutableList<ViewCheckState>

    @Before
    fun before() {
        validator = ChooseInputValidator()
        chooseInputs = mutableListOf()
    }

    @After
    fun tearDown() {
        // do something
    }


    // 검증 성공시
    @Test
    fun testValidateSuccess() {
        val input = "감바스" //비어있지 않고 짧은 입력값
        val result = validator.valid(chooseInputs, input)
        assertTrue(result.isSuccess) // 성공
        assertEquals(0, result.error.size) // 에러 비어있음
    }


    // 입력값이 빈 문자열인경우
    @Test
    fun testValidateFail_With_Empty() {
        val input = "" //빈 문자열
        val result = validator.valid(chooseInputs, input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("input", error.fieldName) // 에러 필드명
        assertEquals(ChooseValidErrorType.EMPTY, error.error) // 에러 값
    }


    // 입력값이 긴경우
    @Test
    fun testValidateFail_With_Long() {
        val input = "SOOOOOO LOOOOONG STRING" //빈 문자열
        val result = validator.valid(chooseInputs, input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("input", error.fieldName) // 에러 필드명
        assertEquals(ChooseValidErrorType.LONG, error.error) // 에러 값
    }

    // 입력값이 중복된경우
    @Test
    fun testValidateFail_With_Duplicate() {
        val input = "SOOOOOO" //빈 문자열

        chooseInputs.add(ViewCheckState(input, false)) // 이미 추가된 체크박스

        val result = validator.valid(chooseInputs, input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("input", error.fieldName) // 에러 필드명
        assertEquals(ChooseValidErrorType.DUPLICATE, error.error) // 에러 값
    }
}