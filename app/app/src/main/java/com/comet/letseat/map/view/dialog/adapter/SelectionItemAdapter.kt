package com.comet.letseat.map.view.dialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.comet.letseat.R
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.databinding.ChooseItemBinding
import com.comet.letseat.map.view.dialog.state.ViewStateViewModel

/**
 * 예측 다이얼로그 / 결과 다이얼로그 공용으로 사용하는 어댑터 클래스.
 * 공용적으로 사용하므로 분리하였음.
 * @param layoutInflater ViewBinding 적용을 위한 inflater
 * @param context resource 가져오기 위한 컨텍스트
 * @param viewModel 각 체크박스 클릭시 변경사항을 알려줄 viewmodel입니다. 화면 회전시 adapter가 갱신되므로 사용해도 괜찮을듯?
 */
class SelectionItemAdapter(private val layoutInflater : LayoutInflater, private val context : Context, private val viewModel : ViewStateViewModel) : RecyclerView.Adapter<SelectionItemViewHolder>() {

    // vm에서 받은 check state. 기본적으로 false로 되어 있으나, 클릭시 vm에서 업데이트 됨. 추후 observe시 업데이트 된 상태 가져올 수 있음.
    private val items: MutableList<ViewCheckState> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionItemViewHolder {
        val view: ChooseItemBinding = ChooseItemBinding.inflate(layoutInflater, parent, false)
        return SelectionItemViewHolder(view, context, viewModel)
    }

    override fun onBindViewHolder(holder: SelectionItemViewHolder, position: Int) {
        holder.bind(items[position], position) // 각 아이템마다 바인딩
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 리사이클러뷰 업데이트
    fun update(updateItems: List<ViewCheckState>) {
        items.clear()
        items.addAll(updateItems)
        notifyDataSetChanged()
    }
}

/**
 * 위 선택 어댑터에서 사용하는 뷰홀더
 * @param view 할당될 뷰
 * @param context resid가져오기 위한 컨텍스트
 * @param onChecked 체크박스 체크시 처리할 로직
 */
class SelectionItemViewHolder(private val view : ChooseItemBinding, private val context: Context, private val viewModel: ViewStateViewModel) : RecyclerView.ViewHolder(view.root), CompoundButton.OnCheckedChangeListener {

        // 기본 초기화전 pos. -1인채로 요청하면 not accept됨
        var pos : Int = -1

        // 각 아이템마다 바인드 될때 할 설정
        fun bind(item : ViewCheckState, pos : Int) {
            view.radio.apply {
                text = item.data // 텍스트 변경
                isChecked = item.isChecked
                setOnCheckedChangeListener(this@SelectionItemViewHolder) // 텍스트 컬러 바꾸는 리스너 변경
                handleColor(isChecked)
            }
            this.pos = pos
        }

        override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
            handleColor(isChecked)
            viewModel.onCheck(pos)
        }

        // 뷰 체크시 컬러 설정
        private fun handleColor(isChecked: Boolean) {
            val color = if (isChecked) context.getColor(R.color.white) else context.getColor(R.color.black)
            view.radio.setTextColor(color)
        }
    }
