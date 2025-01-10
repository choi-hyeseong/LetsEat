package com.comet.letseat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.map.view.MapActivity

// 유저 정보 검증용 액티비티 - 뷰 따로 필요 없을 예정 -> 검증 끝나면 MapActivity로 이동
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "인증완료", Toast.LENGTH_SHORT).show() // temporary
        startActivityWithBackstackClear(MapActivity::class.java)
    }

}

//뒤로가기 금지하고 액티비티 시작하는 확장함수
fun Activity.startActivityWithBackstackClear(targetClass: Class<*>) {
    startActivity(Intent(this, targetClass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)))
}

//기존 getClassName을 썼더니 다른 라이브러리에서 호출되면 tag명이 사라짐.
//따라서 확장 프로퍼티 사용
val Any.TAG: String
    get() = "LETS_EAT_TAG"
