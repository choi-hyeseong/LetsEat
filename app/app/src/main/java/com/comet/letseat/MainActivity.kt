package com.comet.letseat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.comet.letseat.map.view.MapActivity
import com.permissionx.guolindev.PermissionX

// 유저 정보 검증용 액티비티 - 뷰 따로 필요 없을 예정 -> 검증 끝나면 MapActivity로 이동
class MainActivity : AppCompatActivity() {

    companion object {
        // permission constant - 13이후 manifest에서 펄미션 상수 사라짐
        private val PERMISSIONS = listOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission() // 뷰 할당 필요없이 펄미션 체크
    }

    private fun checkPermission() {
        // 펄미션 허용시 init
        if (isLocationPermissionAllEnabled()) {
            init()
            return
        }
        requestPermission()
    }

    private fun requestPermission() {
        // 펄미션 요청
        notifyMessage(R.string.require_gps_permission)
        PermissionX.init(this)
            .permissions(PERMISSIONS)
            .onExplainRequestReason { scope, perms ->
                // 사용자에게 요청 이유 설명
                scope.showRequestReasonDialog(perms, getString(R.string.require_gps_permission), getString(R.string.ok), getString(R.string.close))
            }
            // 요청
            .request { allGranted, _, _ ->
                if (allGranted)
                    init()
                else {
                    // 비허용시
                    notifyMessage(R.string.plz_enable_gps)
                    finish()
                }
            }
    }

    // 펄미션 허용 이후
    private fun init() {
        // TODO
        startActivityWithBackstackClear(MapActivity::class.java)
    }

    // 위치 권한 확인
    private fun isLocationPermissionAllEnabled(): Boolean {
        return PERMISSIONS.map { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED } // permission 루프해서 펄미션 체크 후 granted인지 boolean으로 변환
            .all { granted -> granted } // 전부다 grant를 만족하는지 확인
    }

}

//뒤로가기 금지하고 액티비티 시작하는 확장함수
fun Activity.startActivityWithBackstackClear(targetClass: Class<*>) {
    startActivity(Intent(this, targetClass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)))
}

// activity 전용 사용자에게 notify하는 확장함수 - toast
fun Activity.notifyMessage(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}


//기존 getClassName을 썼더니 다른 라이브러리에서 호출되면 tag명이 사라짐.
//따라서 확장 프로퍼티 사용
val Any.TAG: String
    get() = "LETS_EAT_TAG"
