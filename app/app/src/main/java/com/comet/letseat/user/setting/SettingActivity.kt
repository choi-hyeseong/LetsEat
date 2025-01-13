package com.comet.letseat.user.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.databinding.LayoutSettingBinding

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LayoutSettingBinding = LayoutSettingBinding.inflate(layoutInflater)
        setContentView(view.root)
        // 설정 셋팅
        initSetting(view)
    }

    private fun initSetting(view : LayoutSettingBinding) {
        // 툴바 설정
        view.toolbar.also {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // 뒤로가기 버튼 로직 직접 구현
    // 기존 안드로이드의 방식은 새로운 stack을 생성하므로 이전 스택의 액티비티를 재활용하지 않음. -> 카카오맵 또 로드됨.
    // 따라서 직접 backPress로 호출
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}