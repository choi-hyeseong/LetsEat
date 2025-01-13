package com.comet.letseat.map.view.dialog.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.comet.letseat.R
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.DialogResultBinding
import com.comet.letseat.map.view.dialog.adapter.SelectionItemAdapter
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidErrorType
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator
import java.io.Serializable

class ResultDialog : AbstractDialog<DialogResultBinding>() {

    companion object {

        // dialog request key
        const val REQUEST_KEY : String = "RESULT_DIALOG_KEY"
        // result key
        const val RESULT_KEY : String = "RESULT_DIALOG_RESULT_KEY"
        // input key
        const val INPUT_KEY : String = "RESULT_DIALOG_MENUS"

        /**
         * Dialog를 보여주는 companion 함수
         * @param fragmentManager 보여질 프래그먼트 매니저입니다.
         * @param lifecycleOwner callback을 관리할 lifecycle의 owner입니다.
         * @param callback 요청 성공시 결과를 처리할 콜백입니다. - 콜백의 param은 최종적으로 선택된 메뉴입니다.
         */
        fun show(input : ResultDialogInput, fragmentManager: FragmentManager, lifecycleOwner : LifecycleOwner, callback : (String) -> Unit) {
            val dialog = ResultDialog().apply {
                arguments = bundleOf(INPUT_KEY to input)
            }
            dialog.show(fragmentManager, null) // tag 없이
            fragmentManager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, bundle ->
                val result : String = bundle.getString(RESULT_KEY) ?: return@setFragmentResultListener
                callback(result)
            }
        }

    }

    private val dialogViewModel : ResultDialogViewModel = ResultDialogViewModel(ResultValidator())
    private lateinit var resultAdapter : SelectionItemAdapter

    override fun onResume() {
        super.onResume()
        resize(0.9f, 0.9f)
    }

    override fun initView(bind: DialogResultBinding) {
        // argument init
        val input = requireArguments().getSerializable(INPUT_KEY) as? ResultDialogInput
        if (input == null) {
            notifyMessage(R.string.result_dialog_menu_not_found)
            return
        }
        dialogViewModel.updateMenu(input) // VM에게 전달

        bind.close.setThrottleClickListener {
            // 취소
            dismiss()
        }

        bind.chooseRecycler.apply {
            resultAdapter = SelectionItemAdapter(layoutInflater, requireContext()) { dialogViewModel.onChooseResultSelection(it) } // adapter 할당
            adapter = resultAdapter
            layoutManager = GridLayoutManager(requireContext(), 3) // 3열의 세로 레이아웃
        }

        // 검색 수행
        bind.serach.setThrottleClickListener {
            dialogViewModel.search()
        }

        // search 메소드 호출에 따른 오류 response 흭득
        dialogViewModel.userResultSelectionErrorLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let {event ->
                when(event) {
                    ResultValidErrorType.EMPTY -> notifyMessage(R.string.selection_empty)
                    ResultValidErrorType.MULTIPLE -> notifyMessage(R.string.select_only_one)
                }
            }
        }

        // 결과 데이터 갱신시
        dialogViewModel.userResultLiveData.observe(viewLifecycleOwner) {
            resultAdapter.update(it)
        }

        // 최종 음식 선택 완료시
        dialogViewModel.resultLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let { result ->
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to result)) // bundle에 담아 결과 리턴
                dismiss() //종료
            }
        }




    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogResultBinding {
        return DialogResultBinding.inflate(layoutInflater, container, false)
    }
}

/**
 * 해당 다이얼로그에 표시할 메뉴값들
 * @property menus AI가 예측한 메뉴입니다.
 */
data class ResultDialogInput(val menus : List<String>) : Serializable