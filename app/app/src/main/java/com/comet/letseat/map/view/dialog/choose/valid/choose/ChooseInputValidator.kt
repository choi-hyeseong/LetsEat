package com.comet.letseat.map.view.dialog.choose.valid.choose

import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.valid.ValidationError
import com.comet.letseat.common.view.valid.ValidationResult

/**
 * 선택지의 사용자 입력을 검증하는 클래스입니다.
 */
class ChooseInputValidator {

    /**
     * 입력을 검증합니다.
     * @param list 현재 체크박스가 담긴 리스트입니다. - 빈 리스트를 입력받는 경우 중복 체크를 진행하지 않습니다.
     * @param input 사용자의 입력값입니다.
     * @return 검증 결과를 반환합니다.
     */
    fun valid(list : List<ViewCheckState>, input : String) : ValidationResult<ChooseValidErrorType> {
        val errors : MutableList<ValidationError<ChooseValidErrorType>> = mutableListOf()
        // 입력값이 공백인경우
        if (input.isEmpty())
            errors.add(ValidationError("input", ChooseValidErrorType.EMPTY))

        // 입력값이 긴 경우
        if (input.length >= 10)
            errors.add(ValidationError("input", ChooseValidErrorType.LONG))

        // 중복된경우
        if (list.contains(ViewCheckState(input, false)))
            errors.add(ValidationError("input", ChooseValidErrorType.DUPLICATE))
        return ValidationResult(errors.isEmpty(), errors)
    }
}