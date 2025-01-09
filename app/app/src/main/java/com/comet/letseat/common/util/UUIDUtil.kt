package com.comet.letseat.common.util

import java.util.UUID

/**
 * 문자열을 UUID로 안전하게 바꾸는 확장함수입니다.
 * @return UUID로 변환에 성공한경우 UUID, 실패한경우 null을 리턴합니다.
 */
fun String.toUUID() : UUID? = kotlin.runCatching { UUID.fromString(this) }.getOrNull()