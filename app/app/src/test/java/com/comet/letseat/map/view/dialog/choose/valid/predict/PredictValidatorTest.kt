package com.comet.letseat.map.view.dialog.choose.valid.predict

import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// predict라 되어있으나, 예측전 카테고리 선택 검증
class PredictValidatorTest {


    lateinit var validator: PredictValidator

    @Before
    fun before() {
        validator = PredictValidator()
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
        val input = mutableListOf(ViewCheckState("짠", false), ViewCheckState("달달한", true))
        val result = validator.validate(input)

        assertTrue(result.isSuccess) // 성공
        assertEquals(0, result.error.size) // 에러 비어있음
    }


    // 입력값의 카테고리가 아무것도 선택되지 않은경우
    @Test
    fun testValidateFail_With_Empty() {
        // 짠, 달달한 2개의 카테고리중 둘다 false로 체크되어 있지 않은경우
        val input = mutableListOf(ViewCheckState("짠", false), ViewCheckState("달달한", false))
        val result = validator.validate(input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("checks", error.fieldName) // 에러 필드명
        assertEquals(PredictValidErrorType.EMPTY, error.error) // 에러 값
    }


    // 최대 선택지인 10개를 벗어난경우
    @Test
    fun testValidateFail_With_Exceed_Max() {
        val input = IntRange(0, 11).map { it.toString() }.toState() // 0~10까지 총 11개가 있는 ViewState - 기본값은 false이므로 아무것도 선택되지 않음
        input.forEach { it.isChecked = true } // 따라서 전부 체크 (11개)

        val result = validator.validate(input)
        assertFalse(result.isSuccess)
        assertEquals(1, result.error.size)

        val error = result.error.first()
        assertEquals("checks", error.fieldName) // 에러 필드명
        assertEquals(PredictValidErrorType.TOO_MANY, error.error) // 에러 값
    }
}