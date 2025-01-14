package com.comet.letseat.map.view.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.comet.letseat.R
import com.comet.letseat.TAG
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.databinding.DialogFailBinding
import com.comet.letseat.user.remote.type.NetworkErrorType

class FailDialog : AbstractDialog<DialogFailBinding>() {

    companion object {

        // error key
        const val ERROR_KEY : String = "CHOOSE_DIALOG_RESULT"

        /**
         * Dialog를 보여주는 companion 함수
         * @param fragmentManager 보여질 프래그먼트 매니저입니다.
         * @param errorType 에러 다이얼로그에 전달할 error type입니다.
         */
        fun show(fragmentManager: FragmentManager, errorType : NetworkErrorType) {
            val dialog = FailDialog().apply {
                arguments = bundleOf(ERROR_KEY to errorType)
            }
            dialog.show(fragmentManager, null) // tag 없이
        }

    }
    override fun onResume() {
        super.onResume()
        resize(0.8f, 0.45f)
    }
    override fun initView(bind: DialogFailBinding) {
        bind.closeButton.setOnClickListener {
            dismiss()
        }
        // error message init
        val errorType : NetworkErrorType? = requireArguments().getSerializable(ERROR_KEY) as? NetworkErrorType
        if (errorType == null) {
            // 에러 arg를 가져오지 못하는경우
            Log.w(TAG, "can't get error type in FailDialog")
            return
        }
        // 가져온경우
        bind.errorBody.text = when (errorType) {
            NetworkErrorType.ERROR -> getString(R.string.request_error)
            NetworkErrorType.EXCEPTION -> getString(R.string.request_exception)
        }
    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFailBinding {
        return DialogFailBinding.inflate(inflater, container, false)
    }

}