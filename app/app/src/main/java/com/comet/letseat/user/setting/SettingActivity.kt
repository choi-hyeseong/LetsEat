package com.comet.letseat.user.setting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.R
import com.comet.letseat.common.storage.PreferenceDataStore
import com.comet.letseat.common.util.NetworkUtil
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.LayoutSettingBinding
import com.comet.letseat.notifyMessage
import com.comet.letseat.user.history.view.HistoryActivity
import com.comet.letseat.user.local.repository.PreferenceUserRepository
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.user.api.UserAPI
import com.comet.letseat.user.remote.user.repository.RetrofitRemoteUserRepository
import com.comet.letseat.user.remote.user.usecase.RemoteDeleteUserUseCase
import kotlin.system.exitProcess

class SettingActivity : AppCompatActivity() {

    // todo hilt
    val viewModel : SettingViewModel by lazy {
        val userRepository = PreferenceUserRepository(PreferenceDataStore(this))
        val remoteRepository = RetrofitRemoteUserRepository(NetworkUtil.provideAPI(UserAPI::class.java))
        SettingViewModel(LoadUserUseCase(userRepository), DeleteUserUseCase(userRepository), RemoteDeleteUserUseCase(remoteRepository))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: LayoutSettingBinding = LayoutSettingBinding.inflate(layoutInflater)
        setContentView(view.root)
        // 설정 셋팅
        initSetting(view)
        initObserver()
    }

    private fun initSetting(view: LayoutSettingBinding) {
        // 툴바 설정
        view.toolbar.also {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        view.deleteUser.setThrottleClickListener {
            // 삭제 알림 보여주기
            AlertDialog.Builder(this)
                .setTitle(R.string.delete_user_info)
                .setMessage(R.string.delete_user_info_detail)
                .setPositiveButton(R.string.ok) {_, _ -> viewModel.deleteUser() }
                .setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() } // 종료
                .create()
                .show()
        }

        view.aiHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun initObserver() {
        // 삭제 결과 받기
        viewModel.deleteResponseLiveData.observe(this) { isSuccess ->
            if (isSuccess) {
                // 삭제 성공시
                notifyMessage(R.string.user_info_deleted)
                finishAffinity()
                exitProcess(0)
            }
            else
                // 삭제 실패시
                notifyMessage(R.string.user_delete_failed)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}