package com.comet.letseat.common.view.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// 뷰 상태 체크 상태 변환 확장함수 테스트
class ViewCheckStateTest {

    // 문자열 리스트를 state 리스트로 변환하는 과정 확인
    @Test
    fun testConvertStringToState() {
        val dataList = mutableListOf<String>("데이터 1", "데이터 2", "데이터 3")
        val states = dataList.toState()

        states.forEachIndexed { index, it ->
            assertEquals(dataList[index], it.data) // 원본 인덱스의 위치의 값과 일치하는지
            assertFalse(it.isChecked) // 체크 여부는 기본값인 false인지
        }
    }

    // state 리스트를 문자열 리스트로 변환하는 과정 확인
    @Test
    fun testConvertStateToString() {
        val states = listOf(ViewCheckState("데이터 1", false), ViewCheckState("데이터 2", false), ViewCheckState("데이터 3", false))
        val result = states.convertToStringList()

        states.forEachIndexed { index, it ->
            assertEquals(it.data, result[index])
        }
    }

    // view check state가 같은지
    @Test
    fun testEquals() {
        // 값이 완전히 같은경우
        val first = ViewCheckState("라면", false)
        val second = ViewCheckState("라면", false)
        // 두가지 방식으로 비교
        assertEquals(first, second)
        assertTrue(first == second)

        //체크 상태만 다른경우
        val third = ViewCheckState("라면", false)
        val fourth = ViewCheckState("라면", true)
        // 두가지 방식으로 비교
        assertEquals(third, fourth)
        assertTrue(third == fourth)
    }

    // view check state 다른지 확인
    @Test
    fun testNotEquas() {
        // data가 다른경우
        val first = ViewCheckState("라면", false)
        val second = ViewCheckState("밥", false)
        assertNotEquals(first, second)
        assertFalse(first == second)
    }
}