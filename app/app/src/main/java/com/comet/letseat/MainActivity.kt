package com.comet.letseat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.databinding.ActivityMainBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SDK 초기화
        KakaoMapSdk.init(this, BuildConfig.API_KEY)
        val view : ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)
        initMapView(view)
    }

    private fun initMapView(bind : ActivityMainBinding) {
        val mapLifeCycleCallback : MapLifeCycleCallback = object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                TODO("Not yet implemented")
            }

            override fun onMapError(e: Exception) {
                Log.w(TAG, "Encountered Map Load Error", e)
            }

        }
        val mapReadyCallback : KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                Log.i(TAG, "Map loads complete.")
            }

        }
        bind.kakaoMap.start(mapLifeCycleCallback, mapReadyCallback)
    }

}

//기존 getClassName을 썼더니 다른 라이브러리에서 호출되면 tag명이 사라짐.
//따라서 확장 프로퍼티 사용
val Any.TAG: String
    get() = "LETS_EAT_TAG"
