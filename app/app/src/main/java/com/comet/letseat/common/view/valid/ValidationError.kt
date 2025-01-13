package com.comet.letseat.common.view.valid

/**
 * 검증 오류를 나타내는 클래스 입니다.
 * @property fieldName 오류가 발생한 필드명 입니다.
 * @property error 오류 타입 enum 입니다.
 */
data class ValidationError<T>(val fieldName : String, val error : T)