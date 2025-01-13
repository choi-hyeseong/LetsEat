package com.comet.letseat.map.view.dialog.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comet.letseat.R
import com.comet.letseat.common.dialog.AbstractDialog
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.databinding.ChooseItemBinding
import com.comet.letseat.databinding.DialogResultBinding
import com.comet.letseat.map.view.MapViewModel
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidErrorType
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator

class ResultDialog(private val viewModel: MapViewModel) : AbstractDialog<DialogResultBinding>() {

    private val dialogViewModel : ResultDialogViewModel = ResultDialogViewModel(ResultValidator())
    private lateinit var resultAdapter : ResultItemAdapter

    override fun onResume() {
        super.onResume()
        resize(0.9f, 0.9f)
    }

    override fun initView(bind: DialogResultBinding) {
        bind.close.setThrottleClickListener {
            // 취소
            dismiss()
        }

        bind.chooseRecycler.apply {
            resultAdapter = ResultItemAdapter() // adapter 할당
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

        dialogViewModel.userResultLiveData.observe(viewLifecycleOwner) {
            resultAdapter.update(it)
        }




    }

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogResultBinding {
        return DialogResultBinding.inflate(layoutInflater, container, false)
    }

    private inner class ResultItemAdapter : RecyclerView.Adapter<ResultItemHolder>() {

        // vm에서 받은 check state.
        private val items : MutableList<ViewCheckState> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultItemHolder {
            val view : ChooseItemBinding = ChooseItemBinding.inflate(layoutInflater, parent, false)
            return ResultItemHolder(view)
        }

        override fun onBindViewHolder(holder: ResultItemHolder, position: Int) {
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

    private inner class ResultItemHolder(private val view : ChooseItemBinding) : RecyclerView.ViewHolder(view.root), CompoundButton.OnCheckedChangeListener {

        // 기본 초기화전 pos. -1인채로 요청하면 not accept됨
        var pos : Int = -1

        // 각 아이템마다 바인드 될때 할 설정
        fun bind(item : ViewCheckState, pos : Int) {
            view.radio.apply {
                text = item.data // 텍스트 변경
                isChecked = item.isChecked
                setOnCheckedChangeListener(this@ResultItemHolder) // 텍스트 컬러 바꾸는 리스너 변경
                onCheckedChanged(this, item.isChecked) //아이템 적용
            }
            this.pos = pos
        }

        override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
            val color = if (isChecked) requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black)
            view.radio.setTextColor(color)
            dialogViewModel.onChooseResultSelection(pos) // vm에 저장된 state 업데이트
        }
    }
}