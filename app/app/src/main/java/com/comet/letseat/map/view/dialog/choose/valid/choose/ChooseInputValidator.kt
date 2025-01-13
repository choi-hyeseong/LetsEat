package com.comet.letseat.map.view.dialog.choose.valid.choose

import com.comet.letseat.common.view.valid.ValidationError
import com.comet.letseat.common.view.valid.ValidationResult

/**
 * 선택지의 사용자 입력을 검증하는 클래스입니다.
 */
class ChooseInputValidator {

    /**
     * 입력을 검증합니다.
     * @param input 사용자의 입력값입니다.
     * @return 검증 결과를 반환합니다.
     */
    fun valid(input : String) : ValidationResult<ChooseValidErrorType> {
        val errors : MutableList<ValidationError<ChooseValidErrorType>> = mutableListOf()
        // 입력값이 공백인경우
        if (input.isEmpty())
            errors.add(ValidationError("input", ChooseValidErrorType.EMPTY))

        // 입력값이 긴 경우
        if (input.length >= 10)
            errors.add(ValidationError("input", ChooseValidErrorType.LONG))
        return ValidationResult(errors.isEmpty(), errors)
    }
}