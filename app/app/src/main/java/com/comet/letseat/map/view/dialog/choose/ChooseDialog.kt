package com.comet.letseat.map.view.dialog.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.comet.letseat.R
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.DialogChooseBinding
import com.comet.letseat.map.view.dialog.adapter.SelectionItemAdapter
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseViewValidator
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidErrorType
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class ChooseDialog : AbstractDialog<DialogChooseBinding>() {

    companion object {

        // dialog request key
        const val REQUEST_KEY : String = "CHOOSE_DIALOG_KEY"
        // result key
        const val RESULT_KEY : String = "CHOOSE_DIALOG_RESULT"

        /**
         * Dialog를 보여주는 companion 함수
         * @param fragmentManager 보여질 프래그먼트 매니저입니다.
         * @param lifecycleOwner callback을 관리할 lifecycle의 owner입니다.
         * @param callback 요청 성공시 결과를 처리할 콜백입니다.
         */
        fun show(fragmentManager: FragmentManager, lifecycleOwner : LifecycleOwner, callback : (ChooseResult) -> Unit) {
            ChooseDialog().show(fragmentManager, null) // tag 없이
            fragmentManager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, bundle ->
                val result : ChooseResult = bundle.getSerializable(RESULT_KEY) as? ChooseResult ?: return@setFragmentResultListener
                callback(result)
            }
        }

    }

    private lateinit var itemAdapter : SelectionItemAdapter

    @Inject
    lateinit var validator : ChooseViewValidator

    private val chooseViewModel : ChooseDialogViewModel by viewModels()

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogChooseBinding {
        return DialogChooseBinding.inflate(inflater, container, false)
    }

    // 뷰 초기화
    override fun initView(bind: DialogChooseBinding) {
        // close시
        bind.close.setThrottleClickListener {
            // 취소
            dismiss()
        }

        bind.chooseRecycler.apply {
            itemAdapter = SelectionItemAdapter(layoutInflater, requireContext(), chooseViewModel) // adapter 할당
            adapter = itemAdapter
            layoutManager = GridLayoutManager(requireContext(), 3) // 3열의 세로 레이아웃
        }

        bind.addItem.setThrottleClickListener {
            // 검증 성공시 vm에 요청
            // 만약 이전에 입력한 카테고리를 앱 재실행시에도 사용하게 한다면 구조 변경이 필요할듯
            // 변경한다면 사용자 지정 카테고리 또는 시간대별, 기분별로 분리해놓고 직접 추가하게 하면 깔끔할 것 같은데, 현재 방식에서는 기존 입력 카테고리를 불러오면 좀 가독성이 떨어질수도.
            if (validator.validate(requireContext(), bind))
                chooseViewModel.addCategory(bind.input.text.toString())
        }


        bind.predict.setThrottleClickListener {
            // 예측 요청
            chooseViewModel.predict()
        }

        // add item에 대한 오류 response 흭득
        chooseViewModel.userInputErrorLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let {event ->
                when(event) {
                    ChooseValidErrorType.EMPTY -> notifyMessage(R.string.validation_empty)
                    ChooseValidErrorType.LONG -> notifyMessage(R.string.validation_too_long)
                    ChooseValidErrorType.DUPLICATE -> notifyMessage(R.string.validation_category_duplicate)
                }

            }
        }

        // 유저 선택정보 가져옴 - 기본 데이터 init과 화면 회전시
        chooseViewModel.userSelectionLiveData.observe(viewLifecycleOwner) {
            itemAdapter.update(it)
        }

        // 체크박스 검증 에러시
        chooseViewModel.userCheckboxErrorLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let {event ->
                when(event) {
                    PredictValidErrorType.EMPTY -> notifyMessage(R.string.selection_empty)
                    PredictValidErrorType.TOO_MANY -> notifyMessage(R.string.selection_too_many)
                }

            }
        }

        // 카테고리 선택 완료시
        chooseViewModel.resultLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let { result ->
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to ChooseResult(result))) // bundle에 담아 결과 리턴
                dismiss() //종료
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resize(0.9f, 0.9f)
    }

}


// fragment의 요청 결과 반환용 클래스
data class ChooseResult(val selections : List<String>) : Serializable