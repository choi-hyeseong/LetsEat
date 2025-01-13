package com.comet.letseat.map.view.dialog.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comet.letseat.R
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.databinding.ChooseItemBinding
import com.comet.letseat.databinding.DialogChooseBinding
import com.comet.letseat.map.view.MapViewModel
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseViewValidator
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidator


class ChooseDialog(val viewModel: MapViewModel) : AbstractDialog<DialogChooseBinding>() {

    private lateinit var itemAdapter : ItemAdapter
    // todo hilt - fragment는 생성자 없어야함.
    private val validator = ChooseViewValidator(ChooseInputValidator())
    private val chooseViewModel = ChooseDialogViewModel(ChooseInputValidator(), PredictValidator())

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
            itemAdapter = ItemAdapter() // adapter 할당
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
                }

            }
        }

        chooseViewModel.userSelectionLiveData.observe(viewLifecycleOwner) {
            itemAdapter.update(it)
        }

        chooseViewModel.userCheckboxErrorLiveData.observe(viewLifecycleOwner) {
            it.getContent()?.let {event ->
                when(event) {
                    PredictValidErrorType.EMPTY -> notifyMessage(R.string.selection_empty)
                    PredictValidErrorType.TOO_MANY -> notifyMessage(R.string.selection_too_many)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        resize(0.9f, 0.9f)
    }

    // 선택지를 위한 리사이클러뷰
    private inner class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {

        // vm에서 받은 check state. 기본적으로 false로 되어 있으나, 클릭시 vm에서 업데이트 됨. 추후 observe시 업데이트 된 상태 가져올 수 있음.
        private val items : MutableList<ViewCheckState> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view : ChooseItemBinding = ChooseItemBinding.inflate(layoutInflater, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(items[position], position) // 각 아이템마다 바인딩
        }

        override fun getItemCount(): Int {
            return items.size
        }

        // 리사이클러뷰 업데이트
        fun update(updateItems : List<ViewCheckState>) {
            items.clear()
            items.addAll(updateItems)
            notifyDataSetChanged()
        }
    }

    private inner class ItemViewHolder(private val view : ChooseItemBinding) : RecyclerView.ViewHolder(view.root), OnCheckedChangeListener {

        // 기본 초기화전 pos. -1인채로 요청하면 not accept됨
        var pos : Int = -1

        // 각 아이템마다 바인드 될때 할 설정
        fun bind(item : ViewCheckState, pos : Int) {
            view.radio.apply {
                text = item.data // 텍스트 변경
                isChecked = item.isChecked
                setOnCheckedChangeListener(this@ItemViewHolder) // 텍스트 컬러 바꾸는 리스너 변경
                onCheckedChanged(this, item.isChecked) // item 데이터 적용
            }
            this.pos = pos
        }

        override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
            val color = if (isChecked) requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black)
            view.radio.setTextColor(color)
            chooseViewModel.onChooseDialogCheck(pos) // vm에 저장된 state 업데이트
        }
    }

}