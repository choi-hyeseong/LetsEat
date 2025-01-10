package com.comet.letseat.common.livedata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventTest {


    // 기존에 가져온적이 없는 컨텐츠 가져오기 - 성공
    @Test
    fun testGetContentSuccess() {
        val data = "SUCCESS"
        val event = Event(data)

        val result = event.getContent()
        assertNotNull(result)
        assertEquals(data, result)
    }

    // 기존에 가져온 컨텐츠 가져오기 - 실패, null
    @Test
    fun testGetContentFailure() {
        val data = "FAIL"
        val event = Event(data)

        event.getContent() // 데이터 한번 가져오기

        val result = event.getContent()
        assertNull(result)
        assertNotEquals(data, result)
    }

    // 기존에 가져온것도 강제로 또 가져오기
    @Test
    fun testGetContentForce() {
        val data = "SUCCESS"
        val event = Event(data)
        event.getContent()

        val result = event.getContentForce()
        assertNotNull(result)
        assertEquals(data, result)
    }

    // isHandled 변수 확인
    @Test
    fun testIsHandled() {
        val data = "DATA"
        val event = Event(data)

        assertFalse(event.isHandled)
        event.getContent()
        assertTrue(event.isHandled)
    }

}