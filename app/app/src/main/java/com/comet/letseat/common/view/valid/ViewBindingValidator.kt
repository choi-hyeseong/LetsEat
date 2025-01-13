package com.comet.letseat.common.view.valid

import android.content.Context
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding으로 제공된 클래스를 검증하는 validator
 * 추가적인 클래스를 이용해서 검증에 사용할 수 있습니다. VM에서 검증용 validator를 여기서 쓴다던가..
 */
interface ViewBindingValidator<T : ViewBinding> {

    /**
     * 해당 뷰를 검증합니다.
     * @param context 리소스 id(문자열)등에 접근하기 위한 컨텍스트 입니다.
     * @param view 검증할 뷰입니다.
     * @return 검증 성공여부를 반환합니다.
     */
    fun validate(context : Context, view : T) : Boolean
}