package com.comet.letseat.map.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.BuildConfig
import com.comet.letseat.R
import com.comet.letseat.TAG
import com.comet.letseat.databinding.LayoutMapBinding
import com.comet.letseat.map.gps.dao.LocationDao
import com.comet.letseat.map.gps.repository.NetworkLocationRepository
import com.comet.letseat.map.gps.usecase.GetLocationUseCase
import com.comet.letseat.map.gps.usecase.GpsEnabledUseCase
import com.comet.letseat.notifyMessage
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory

class MapActivity : AppCompatActivity() {

    private var kakaoMap: KakaoMap? = null

    // TODO hilt
    private val viewModel: MapViewModel by lazy {
        val gpsDao = LocationDao(this)
        val locationRepository = NetworkLocationRepository(gpsDao)
        MapViewModel(GpsEnabledUseCase(locationRepository), GetLocationUseCase(locationRepository))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.API_KEY)

        val view: LayoutMapBinding = LayoutMapBinding.inflate(layoutInflater)
        setContentView(view.root)

        initMapView(view)
        // TODO GPS BUTTON
        initObserver()
    }

    private fun initMapView(bind: LayoutMapBinding) {
        val mapLifeCycleCallback: MapLifeCycleCallback = object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // non handle
                kakaoMap = null
            }

            override fun onMapError(e: Exception) {
                Log.w(TAG, "Encountered Map Load Error", e)
                notifyMessage(R.string.map_load_error)
                kakaoMap = null
            }

        }
        val mapReadyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                Log.i(TAG, "Map loads complete.")
                kakaoMap = map
                viewModel.loadLocation()
            }

        }
        bind.kakaoMap.start(mapLifeCycleCallback, mapReadyCallback)
    }

    private fun initObserver() {
        viewModel.locationLiveData.observe(this) { location ->
            // 카카오맵이 로드되지 않은경우는 리턴
            if (kakaoMap == null) {
                notifyMessage(R.string.kakao_map_not_loaded)
                return@observe
            }
            val update = CameraUpdateFactory.newCenterPosition(location.toLatlng())
            kakaoMap?.moveCamera(update)
        }

        viewModel.gpsResponseErrorLiveData.observe(this) { event ->
            // TODO GPS ERROR
        }
    }
}