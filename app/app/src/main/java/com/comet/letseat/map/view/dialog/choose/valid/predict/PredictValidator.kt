package com.comet.letseat.map.view.dialog.choose.valid.predict

import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.valid.ValidationError
import com.comet.letseat.common.view.valid.ValidationResult

/**
 * 가게 예측에서 입력값을 검증하기 위한 클래스 - 체크박스의 상태는 VM에서 관리되므로 view validator는 구현하지 않았음
 */
class PredictValidator {

    companion object {
        // 최대 선택가능 카테고리 수
        private const val MAX_CHECK = 10
    }

    fun validate(checks : List<ViewCheckState>) : ValidationResult<PredictValidErrorType> {
        val error : MutableList<ValidationError<PredictValidErrorType>> = mutableListOf()
        val isNonChecks = checks.all { !it.isChecked }
        // 하나도 체크되지 않은경우
        if (isNonChecks)
            error.add(ValidationError("checks", PredictValidErrorType.EMPTY))

        val checkCount = checks.filter { it.isChecked }.size
        // 최대 체크 가능 수 넘어 선 경우
        if (checkCount > MAX_CHECK)
            error.add(ValidationError("checks", PredictValidErrorType.TOO_MANY))

        return ValidationResult(error.isEmpty(), error)
    }
}