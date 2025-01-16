package com.comet.letseat.user.history.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comet.letseat.R
import com.comet.letseat.common.storage.PreferenceDataStore
import com.comet.letseat.common.toDateString
import com.comet.letseat.common.toTimeString
import com.comet.letseat.common.util.NetworkUtil
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.HistoryItemBinding
import com.comet.letseat.databinding.LayoutHistoryBinding
import com.comet.letseat.notifyMessage
import com.comet.letseat.user.history.view.dialog.HistoryDetailDialog
import com.comet.letseat.user.local.repository.PreferenceUserRepository
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.type.NetworkErrorType
import com.comet.letseat.user.remote.user.api.UserAPI
import com.comet.letseat.user.remote.user.model.UserHistory
import com.comet.letseat.user.remote.user.repository.RetrofitRemoteUserRepository
import com.comet.letseat.user.remote.user.usecase.GetUserHistoryUseCase
import java.util.Date

// ai 메뉴 이력 확인 액티비티. 툴바 사용하는 액티비티들은 abstract로 뽑아서 합쳐도 괜찮을듯.
class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter

    // todo hilt
    private val viewModel : HistoryViewModel by lazy {
        val repo = PreferenceUserRepository(PreferenceDataStore(this))
        val remoteRepo = RetrofitRemoteUserRepository(NetworkUtil.provideAPI(UserAPI::class.java))
        HistoryViewModel(LoadUserUseCase(repo), GetUserHistoryUseCase(remoteRepo))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: LayoutHistoryBinding = LayoutHistoryBinding.inflate(layoutInflater)
        setContentView(view.root)
        // 설정 셋팅
        initSetting(view)
        initObserver(view)
        viewModel.loadHistory()
    }

    private fun initSetting(view: LayoutHistoryBinding) {
        // 툴바 설정
        view.toolbar.also {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        view.historyView.apply {
            historyAdapter = HistoryAdapter()
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun initObserver(view : LayoutHistoryBinding) {
        viewModel.historyLiveData.observe(this) {
            // 데이터가 있는 경우와 없는경우 메시지 보여주기
            view.noneData.visibility  = if (it.isEmpty())
                View.VISIBLE
            else
                View.INVISIBLE
            historyAdapter.update(it) // history update
        }

        viewModel.errorLiveData.observe(this) {
            it.getContent()?.let { error ->
                when(error) {
                    NetworkErrorType.ERROR -> notifyMessage(R.string.request_error)
                    NetworkErrorType.EXCEPTION -> notifyMessage(R.string.request_exception)
                }
            }
        }
    }

    private inner class HistoryAdapter : RecyclerView.Adapter<HistoryViewHolder>() {

        private val histories : MutableList<UserHistory> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view : HistoryItemBinding = HistoryItemBinding.inflate(layoutInflater, parent, false)
            return HistoryViewHolder(view)
        }

        override fun getItemCount(): Int {
           return histories.size
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.bind(histories[position])
        }

        // observe시 내부 데이터 업데이트
        fun update(userHistories : List<UserHistory>) {
            histories.apply {
                clear()
                addAll(userHistories)
                notifyDataSetChanged()
            }
        }

    }

    private inner class HistoryViewHolder(private val view: HistoryItemBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(history : UserHistory) {
            val date = Date(history.timeStamp) //날짜 초기화
            view.date.text = getString(R.string.date_format, date.toDateString()) // 날짜 할당
            view.time.text = getString(R.string.time_format, date.toTimeString()) // 시간 할당
            view.result.text = getString(R.string.recommend_result, history.menus?.size) // 비어 있을 수 있음
            view.storeItem.setThrottleClickListener {
                HistoryDetailDialog.show(history, supportFragmentManager) // 상세 다이얼로그 보여주기
            }
        }
    }
}