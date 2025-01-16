package com.comet.letseat.user.history.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.comet.letseat.R
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.common.toDateString
import com.comet.letseat.common.toTimeString
import com.comet.letseat.databinding.DialogHistoryBinding
import com.comet.letseat.user.remote.user.model.UserHistory
import java.util.Date

class HistoryDetailDialog : AbstractDialog<DialogHistoryBinding>() {

    companion object {

        private const val INPUT_PARAM : String = "HISTORY_INPUT"

        // argument 셋업 및 보여주기
        fun show(history : UserHistory, fragmentManager: FragmentManager) {
            val dialog = HistoryDetailDialog().apply {
                arguments = bundleOf(INPUT_PARAM to history) // argument setup
            }
            dialog.show(fragmentManager, null) // 태그 미지정
        }
    }

    override fun onResume() {
        super.onResume()
        resize(0.9f, 0.9f)
    }

    override fun initView(bind: DialogHistoryBinding) {
        val history = requireArguments().getSerializable(INPUT_PARAM) as? UserHistory
        if (history == null) {
            notifyMessage(R.string.history_dialog_argument_invalid)
            dismiss()
        }

        history!!.let {
            val date = Date(it.timeStamp)
            bind.dateTime.text = getString(R.string.date_format, date.toDateString())
            bind.convertTime.text = getString(R.string.time_format, date.toTimeString())
            bind.uuid.text = it.uuid.toString()
            bind.category.text = it.categories.toString()
            bind.statusCode.text = it.statusCode
            bind.recommend.text = it.menus?.toString()
        }

        bind.close.setOnClickListener {
            dismiss() //종료버튼
        }
    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogHistoryBinding {
        return DialogHistoryBinding.inflate(inflater, container, false)
    }
}