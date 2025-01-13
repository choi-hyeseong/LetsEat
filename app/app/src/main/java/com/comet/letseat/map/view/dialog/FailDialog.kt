package com.comet.letseat.map.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.databinding.DialogFailBinding

class FailDialog : AbstractDialog<DialogFailBinding>() {

    override fun onResume() {
        super.onResume()
        resize(0.8f, 0.45f)
    }
    override fun initView(bind: DialogFailBinding) {
        bind.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogFailBinding {
        return DialogFailBinding.inflate(inflater, container, false)
    }

}