package com.comet.letseat.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class UUIDUtilTest {

    // uuid 변환에 성공한경우
    @Test
    fun testConvertUUIDSuccess() {
        // random uuid
        val randomUUID = UUID.randomUUID()
        // string
        val stringUUID = randomUUID.toString()
        // convert
        val convertUUID = stringUUID.toUUID()
        assertNotNull(convertUUID)
        assertEquals(randomUUID, convertUUID)
    }

    // uuid 변환에 실패한경우
    @Test
    fun testConvertUUIDFailure() {
        // non uuid
        val coolUUIDString = "COOL_UUID" //invalid
        // convert
        val convertUUID = coolUUIDString.toUUID()
        assertNull(convertUUID)
    }
}