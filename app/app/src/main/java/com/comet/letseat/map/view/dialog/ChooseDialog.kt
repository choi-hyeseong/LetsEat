package com.comet.letseat.map.view.dialog

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
import com.comet.letseat.databinding.ChooseItemBinding
import com.comet.letseat.databinding.DialogChooseBinding
import com.comet.letseat.map.view.MapViewModel


class ChooseDialog(val viewModel: MapViewModel) : AbstractDialog<DialogChooseBinding>() {

    private lateinit var itemAdapter : ItemAdapter

    override fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): DialogChooseBinding {
        return DialogChooseBinding.inflate(inflater, container, false)
    }

    // 뷰 초기화
    override fun initView(bind: DialogChooseBinding) {
        // close시
        bind.close.setThrottleClickListener {
            dismiss()
        }
        bind.chooseRecycler.apply {
            itemAdapter = ItemAdapter() // adapter 할당
            adapter = itemAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        viewModel.userSelectionLiveData.observe(viewLifecycleOwner) {
            itemAdapter.update(it)
        }
    }

    override fun onResume() {
        super.onResume()
        resize(1f, 0.9f)
    }

    // 선택지를 위한 리사이클러뷰
    private inner class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {

        private val items : MutableList<String> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view : ChooseItemBinding = ChooseItemBinding.inflate(layoutInflater, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(items[position]) // 각 아이템마다 바인딩
        }

        override fun getItemCount(): Int {
            return items.size
        }

        // 리사이클러뷰 업데이트
        fun update(updateItems : List<String>) {
            items.clear()
            items.addAll(updateItems)
            notifyDataSetChanged()
        }
    }

    private inner class ItemViewHolder(private val view : ChooseItemBinding) : RecyclerView.ViewHolder(view.root), OnCheckedChangeListener {

        // 각 아이템마다 바인드 될때 할 설정
        fun bind(text : String) {
            view.radio.apply {
                setText(text) // 텍스트 변경
                setOnCheckedChangeListener(this@ItemViewHolder) // 텍스트 컬러 바꾸는 리스너 변경
            }
        }

        override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
            val color = if (isChecked) requireContext().getColor(R.color.white) else requireContext().getColor(R.color.black)
            view.radio.setTextColor(color)
        }
    }
}