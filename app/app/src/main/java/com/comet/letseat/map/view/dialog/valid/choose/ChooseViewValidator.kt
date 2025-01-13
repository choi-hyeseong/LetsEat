package com.comet.letseat.map.view.dialog.valid.choose

import android.content.Context
import com.comet.letseat.R
import com.comet.letseat.common.view.valid.ViewBindingValidator
import com.comet.letseat.databinding.DialogChooseBinding

/**
 * 선택지 다이얼로그 뷰 검증용 클래스
 * @property chooseInputValidator VM에서도 사용되는 validator를 이용해서 이중으로 검증을 수행합니다.
 */
class ChooseViewValidator(private val chooseInputValidator: ChooseInputValidator) : ViewBindingValidator<DialogChooseBinding> {

    // 뷰 핸들링 및 성공여부 반환
    override fun validate(context : Context, view: DialogChooseBinding) : Boolean {
        val editText = view.input
        val input = editText.text.toString()

        val result = chooseInputValidator.valid(input)
        // 성공한경우 리턴
        if (result.isSuccess)
            return true

        // 실패한 경우
        val error = result.error
        // 이런 경우는 없겠지만, 에러 결과값이 비어있는 경우
        if (error.isEmpty()) {
            editText.error = context.getString(R.string.validation_error)
            return false
        }

        when (error.first().error) {
            ChooseValidError.EMPTY -> editText.error = context.getString(R.string.validation_empty)
            ChooseValidError.LONG -> editText.error = context.getString(R.string.validation_too_long)
        }
        return false

    }
}