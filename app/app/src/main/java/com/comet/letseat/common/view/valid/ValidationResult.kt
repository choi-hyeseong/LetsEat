package com.comet.letseat.common.view.valid

/**
 * 검증 결과를 반환하는 클래스 입니다. 제네릭을 이용해 에러를 나타내는 enum을 제공합니다.
 * @property isSuccess 성공 여부를 반환합니다.
 * @property error 검증에 실패한 필드가 있을경우 반환합니다. 없을경우 empty 입니다.
 */
data class ValidationResult<T : Enum<*>>(val isSuccess : Boolean, val error : List<ValidationError<T>>)