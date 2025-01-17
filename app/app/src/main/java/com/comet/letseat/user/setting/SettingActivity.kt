package com.comet.letseat.user.setting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.R
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.LayoutSettingBinding
import com.comet.letseat.notifyMessage
import com.comet.letseat.user.history.view.HistoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    val viewModel : SettingViewModel by viewModels()

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
            // 삭제 요청걸고 뒤로 가기 누르는 경우? - 서버 요청은 전송 되었으니 강제로 빠져나가는경우 유저 정보를 먼저 삭제 or api 요청 결과 올때까지 기다리지 않고 바로 삭제 후 종료 방법도 있음.
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