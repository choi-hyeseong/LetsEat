package com.comet.letseat.map.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.comet.letseat.BuildConfig
import com.comet.letseat.R
import com.comet.letseat.TAG
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.LayoutMapBinding
import com.comet.letseat.map.gps.dao.LocationDao
import com.comet.letseat.map.gps.repository.NetworkLocationRepository
import com.comet.letseat.map.gps.usecase.GetLocationUseCase
import com.comet.letseat.map.gps.usecase.GpsEnabledUseCase
import com.comet.letseat.map.view.dialog.result.ResultDialog
import com.comet.letseat.map.view.type.GPSErrorType
import com.comet.letseat.notifyMessage
import com.comet.letseat.user.setting.SettingActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle

class MapActivity : AppCompatActivity() {

    private var kakaoMap: KakaoMap? = null // 초기화된 카카오맵. null일경우 미초기화
    private var centerLabel: Label? = null // 현재 자신의 위치 나타내는 라벨 (점) null일경우 미초기화

    // TODO hilt
    private val viewModel: MapViewModel by lazy {
        val gpsDao = LocationDao(this)
        val locationRepository = NetworkLocationRepository(gpsDao)
        MapViewModel(GpsEnabledUseCase(locationRepository), GetLocationUseCase(locationRepository))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 카카오 sdk 초기화
        KakaoMapSdk.init(this, BuildConfig.API_KEY)

        // 뷰 초기화
        val view: LayoutMapBinding = LayoutMapBinding.inflate(layoutInflater)
        setContentView(view.root)

        initView(view)
        initObserver()
    }

    // 맵뷰 초기화
    private fun initView(bind: LayoutMapBinding) {
        bind.kakaoMap.start(KakaoMapErrorCallback(), MapLoadCallback()) // kakao map 초기화

        // 쓰로틀링된 클릭 리스너
        bind.gpsButton.setThrottleClickListener(500L) {
            viewModel.loadLocation() // 위치 요청
        }

        bind.settingButton.setThrottleClickListener {
            // 설정 액티비티로 이동 - 백스택 유지
            startActivity(Intent(this, SettingActivity::class.java))
        }

        bind.search.setThrottleClickListener {
            // 검색 다이얼로그 호출
            ResultDialog(viewModel).show(supportFragmentManager, "ChooseDialog")

        }
    }

    // 내위치 표시하는 점 초기화
    private fun initLabel() {
        if (kakaoMap == null)
            return

        val layer = kakaoMap!!.labelManager?.layer
        if (layer == null) {
            // 레이어를 가져올 수 없는 경우
            Log.w(TAG, "Can't get label layer")
            notifyMessage(R.string.can_not_display_current_location)
            return
        }

        val currentPosition = kakaoMap!!.getCenterPositionOrElse(LatLng.from(35.0, 127.0)) // 현재 카카오맵 중심 좌표. 못가져오는경우 대한민국 좌표
        // 라벨 초기화
        val labelOptions = LabelOptions.from("centerLabel", currentPosition).apply {
            setStyles(LabelStyle.from(R.drawable.gps_my_position).setAnchorPoint(0.5f, 0.5f))
            rank = 1
        }
        centerLabel = layer.addLabel(labelOptions) // 라벨 추가
    }

    private fun initObserver() {
        // gps 정보 로드시
        viewModel.locationLiveData.observe(this) { location ->
            // 카카오맵이 로드되지 않은경우는 리턴
            if (kakaoMap == null) {
                notifyMessage(R.string.kakao_map_not_loaded)
                return@observe
            }

            val update = CameraUpdateFactory.newCenterPosition(location.toLatlng()) // 위치 업데이트
            kakaoMap?.moveCamera(update) // not null임. 카메라 움직임
            centerLabel?.moveTo(location.toLatlng()) // 라벨따라 움직임
        }

        viewModel.gpsResponseErrorLiveData.observe(this) { event ->
            // 이벤트가 처리되지 않은경우 (1회성)
            event.getContent()?.let {
                when(it) {
                    GPSErrorType.NOT_ENABLED -> notifyMessage(R.string.gps_not_enabled)
                    GPSErrorType.LOAD_FAIL -> notifyMessage(R.string.gps_load_failed)
                    GPSErrorType.INTERNAL -> notifyMessage(R.string.gps_internal_error)
                }
            }
        }
    }

    // 맵 lifecycle중 삭제 - 오류 난 경우 할당용 콜백
    private inner class KakaoMapErrorCallback : MapLifeCycleCallback() {
        override fun onMapDestroy() {
            // 맵 destory시 (onDestory 호출시 등등)
            kakaoMap = null
        }

        override fun onMapError(e: Exception) {
            // 맵 로드 실패시 - sdk error등
            Log.w(TAG, "Encountered Map Load Error", e)
            notifyMessage(R.string.map_load_error)
            kakaoMap = null
        }

    }

    // 맵 로드시 콜백
    private inner class MapLoadCallback : KakaoMapReadyCallback() {
        override fun onMapReady(map: KakaoMap) {
            // 맵이 정상적으로 로드시
            Log.i(TAG, "Map loads complete.")
            kakaoMap = map
            // 라벨 초기화
            initLabel()
            // vm에서 gps 로드 요청
            viewModel.loadLocation()
        }

    }
}

/**
 * 카카오맵의 현재 중심 좌표를 가져오거나, 가져오지 못하는경우 대안 LatLng(Param)을 가져오는 확장함수
 */
fun KakaoMap.getCenterPositionOrElse(instead : LatLng) : LatLng = cameraPosition?.position ?: instead
