package com.comet.letseat.common

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 날짜 변환 유탈 테스트 클래스
class DateTimeUtilKtTest {

    companion object {

        // 포맷에 사용되는 날짜 포맷
        private const val DATE_FORMAT = "yyyy-MM-dd"
        // 포맷에 사용되는 시간 포맵
        private const val TIME_FORMAT = "HH:mm"

    }

    // 테스트에 사용될 날짜 포맷용 클래스
    lateinit var dateFormatter : SimpleDateFormat

    // 테스트에 사용되는 시간 포맷용 클래스
    lateinit var timeFormatter: SimpleDateFormat

    @Before
    fun before() {
        // 한국 시간대로 포맷팅 수정
        dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
        timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.KOREA)

    }

    @Test
    fun testFormatDate() {
        val date = Date(System.currentTimeMillis()) // 현재 시간

        val expectedDate = dateFormatter.format(date)
        val expectedTime  = timeFormatter.format(date)

        assertEquals(expectedDate, date.toDateString())
        assertEquals(expectedTime, date.toTimeString())
    }

}