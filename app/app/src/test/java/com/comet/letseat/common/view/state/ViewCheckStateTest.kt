package com.comet.letseat.common.view.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}