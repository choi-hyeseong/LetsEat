package com.comet.letseat.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA) // 날짜 포맷
private val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA) // 시간 포맷

/**
 * 날짜 변환하기 위한 클래스로, 확장함수를 통해 Date로 변환된 시간을 각 조건에 맞게 변환 후 문자열로 출력
 */
fun Date.toDateString() : String = dateFormat.format(this)

fun Date.toTimeString() : String = timeFormat.format(this)