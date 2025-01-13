package com.comet.letseat.map.view.dialog.result.valid.result

import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.valid.ValidationError
import com.comet.letseat.common.view.valid.ValidationResult

/**
 * 가게 예측 결과에서 선택지 검증을 위한 validator - 얘 또한 VM에서 상태가 관리되므로 따로 view validate는 하지 않음
 */
class ResultValidator {

    // 여기서는 하나만 체크된지 확인 -
    fun validate(checks : List<ViewCheckState>) : ValidationResult<ResultValidErrorType> {
        val error : MutableList<ValidationError<ResultValidErrorType>> = mutableListOf()
        val isNonChecks = checks.all { !it.isChecked }
        // 하나도 체크되지 않은경우
        if (isNonChecks)
            error.add(ValidationError("checks", ResultValidErrorType.EMPTY))

        // 여러개 체크된경우 - 뷰에서 안되게 다루긴 할 예정
        val checkCount = checks.filter { it.isChecked }.size
        if (checkCount > 1)
            error.add(ValidationError("checks", ResultValidErrorType.MULTIPLE))

        return ValidationResult(error.isEmpty(), error)
    }
}