package com.comet.letseat.map.view.dialog.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.databinding.DialogLoadingBinding

/**
 * 서버 결과값 로딩 다이얼로그
 */
class LoadingDialog : AbstractDialog<DialogLoadingBinding>() {

    override fun onResume() {
        super.onResume()
        resize(0.6f, 0.35f)
    }

    override fun initView(bind: DialogLoadingBinding) {
        isCancelable = false
    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(inflater, container, false)
    }

}