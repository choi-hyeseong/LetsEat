package com.comet.letseat.map.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.BuildConfig
import com.comet.letseat.TAG
import com.comet.letseat.databinding.LayoutMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.API_KEY)

        val view : LayoutMapBinding = LayoutMapBinding.inflate(layoutInflater)
        setContentView(view.root)

        initMapView(view)
    }

    private fun initMapView(bind : LayoutMapBinding) {
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