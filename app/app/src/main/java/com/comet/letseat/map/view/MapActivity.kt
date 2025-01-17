package com.comet.letseat.map.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.comet.letseat.BuildConfig
import com.comet.letseat.R
import com.comet.letseat.TAG
import com.comet.letseat.common.view.setThrottleClickListener
import com.comet.letseat.databinding.LayoutMapBinding
import com.comet.letseat.databinding.StoreItemBinding
import com.comet.letseat.map.kakao.model.Store
import com.comet.letseat.map.view.dialog.FailDialog
import com.comet.letseat.map.view.dialog.LoadingDialog
import com.comet.letseat.map.view.dialog.choose.ChooseDialog
import com.comet.letseat.map.view.dialog.result.ResultDialog
import com.comet.letseat.map.view.dialog.result.ResultDialogInput
import com.comet.letseat.map.view.type.GPSErrorType
import com.comet.letseat.notifyMessage
import com.comet.letseat.user.setting.SettingActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextStyle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private var kakaoMap: KakaoMap? = null // 초기화된 카카오맵. null일경우 미초기화
    private var centerLabel: Label? = null // 현재 자신의 위치 나타내는 라벨 (점) null일경우 미초기화

    private var loadingDialog: LoadingDialog? = null

    private val markers : MutableList<Label> = mutableListOf() // 가게 나타내는 마커 목록

    // 추후에 초기화될 가게 목록 어댑터
    private lateinit var storeAdapter : StoreAdapter

    // 뷰에서 나타나는 하단 드로워 초기화
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 카카오 sdk 초기화
        KakaoMapSdk.init(this, BuildConfig.API_KEY)

        // 뷰 초기화
        val view: LayoutMapBinding = LayoutMapBinding.inflate(layoutInflater)
        setContentView(view.root)

        initView(view)
        initObserver(view)
    }

    // 만약 화면 회전, 홈화면등과 같이 중단되는 사유가 있을경우 dialog 취소
    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
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
            ChooseDialog.show(supportFragmentManager, this) { result ->
                viewModel.predict(result.selections)
            }
        }

        // 리사이클러뷰 초기화
        bind.stores.apply {
            storeAdapter = StoreAdapter()
            adapter = storeAdapter
            layoutManager = LinearLayoutManager(this@MapActivity)
        }

        // 드로워 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(bind.bottomDrawer)
    }

    // 내위치 표시하는 점 초기화
    private fun initLabel() {
        val currentPosition = kakaoMap!!.getCenterPositionOrElse(LatLng.from(35.0, 127.0)) // 현재 카카오맵 중심 좌표. 못가져오는경우 대한민국 좌표
        // 라벨 초기화
        val labelOptions = LabelOptions.from("centerLabel", currentPosition).apply {
            setStyles(LabelStyle.from(R.drawable.gps_my_position).setAnchorPoint(0.5f, 0.5f))
            rank = 1
        }
        val insertedLabel = addLabel(labelOptions)
        if (insertedLabel == null)
            notifyMessage(R.string.can_not_display_current_location)
        else
            centerLabel = insertedLabel
    }

    /**
     * 라벨 추가하는 메소드
     * @return 성공한경우 추가된 라벨, 실패한경우 null
     */
    private fun addLabel(labelOptions: LabelOptions) : Label? {
        if (kakaoMap == null)
            return null

        val layer = kakaoMap!!.labelManager?.layer
        if (layer == null) {
            // 레이어를 가져올 수 없는 경우
            Log.w(TAG, "Can't get label layer")
            return null
        }
        return layer.addLabel(labelOptions) // 라벨 추가
    }

    private fun initObserver(view : LayoutMapBinding) {
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

        // 로딩 요청시
        viewModel.loadingLiveData.observe(this) { event ->
            event.getContent()?.let { isLoading ->
                if (isLoading)
                    loadingDialog = LoadingDialog().also { it.show(supportFragmentManager, null) }
                else
                    loadingDialog?.dismiss() // false로 로딩되는 경우 한정해서 nullable하게
            }
        }

        // 예측 성공시
        viewModel.predictLiveData.observe(this) { event ->
            event.getContent()?.let { menus ->
                ResultDialog.show(ResultDialogInput(menus), supportFragmentManager, this) { keyword ->
                    if (kakaoMap == null)
                        return@show
                    val latLng : LatLng = kakaoMap!!.getCenterPositionOrElse(LatLng.from(35.0, 127.0)) // 카카오맵의 중심 좌표 가져오기
                    viewModel.findStores(latLng.longitude, latLng.latitude, keyword)
                }
            }
        }

        // 요청중 네트워크 오류를 받은경우
        viewModel.predictNetworkErrorLiveData.observe(this) {
            it.getContent()?.let { error ->
                FailDialog.show(supportFragmentManager, error)
            }

        }

        viewModel.storeLiveData.observe(this) {
            // 가게 목록이 없는경우 - 없다고 나타내기
            if (it.isEmpty()) {
                view.noneData.visibility = View.VISIBLE
                return@observe
            }
            // 가게 목록이 있는경우
            view.noneData.visibility = View.INVISIBLE
            createLocationMarkers(it) // 마커 추가
            storeAdapter.update(it) // 리사이클러뷰 업데이트
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // 확장
        }

        // 카카오맵 api 호출 오류 notify
        viewModel.storeNetworkErrorLiveData.observe(this) {
            it.getContent()?.let {
                notifyMessage(R.string.kakao_map_stores_api_error)
            }
        }
    }

    private fun createLocationMarkers(stores : List<Store>) {
        if (kakaoMap == null) {
            notifyMessage(R.string.map_load_error)
            return
        }

        // 기존 마커 제거
        markers.forEach { it.remove() }

        val storeMarkers = stores.mapNotNull { store ->
            val labelOptions = LabelOptions.from(LatLng.from(store.latitude, store.longitude)).apply {
                // 스타일 설정
                setStyles(
                    LabelStyle.from(R.drawable.marker_image)
                        .setAnchorPoint(0.5f, 0.5f)
                        .setTextStyles(LabelTextStyle.from(25, R.color.black)))
                // 가게 이름 설정
                setTexts(store.name)
                rank = 1
            }
            addLabel(labelOptions) // 지도에 라벨 추가
        }
        markers.addAll(storeMarkers)


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

    /**
     * 가게 정보 표시하기 위한 어댑터
     */
    private inner class StoreAdapter : RecyclerView.Adapter<StoreViewHolder>() {

        // 내부에 표시될 가게 정보
        private val stores : MutableList<Store> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
            val bind : StoreItemBinding = StoreItemBinding.inflate(layoutInflater, parent, false)
            return StoreViewHolder(bind)
        }

        override fun getItemCount(): Int {
            return stores.size
        }

        override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
            holder.bind(stores[position])
        }

        fun update(store : List<Store>) {
            this.stores.apply {
                clear()
                addAll(store)
                notifyDataSetChanged()
            }
        }

    }

    /**
     * 각 가게 정보를 나타내는 뷰홀더
     */
    private inner class StoreViewHolder(private val bind : StoreItemBinding) : RecyclerView.ViewHolder(bind.root) {

        // 각 홀더마다 갖고 있는 가게 정보 - 초기화됨
        lateinit var holderStore : Store

        // 각 가게 정보 업데이트
        fun bind(store : Store) {
            bind.name.text = store.name
            bind.distance.text = parseDistance(store.distance)
            bind.address.text = store.address
            if (store.phone.isNotEmpty())
                bind.phone.text = store.phone
            holderStore = store // 초기화
            // 뷰 클릭시
            bind.storeItem.setThrottleClickListener(this::onViewClick)
        }

        // 거리 변환 함수 - m는 그냥 표시, km는 점단위로 변환
        fun parseDistance(distance : Double) : String {
            return if (distance < 1000)
                getString(R.string.distance_meter, distance.toInt())
            else {
                val km = (distance / 1000).toInt() // 2300m -> 2km
                val meter = ((distance - (1000 * km)) / 100).toInt() // 2300m -> (2300 - 2000) / 100 -> 0.3 m
                getString(R.string.distance_kilometer, "$km.$meter")
            }
        }

        fun onViewClick(view : View) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED // 접기
            val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(holderStore.latitude, holderStore.longitude))
            kakaoMap?.moveCamera(cameraUpdate)
        }
    }
}

/**
 * 카카오맵의 현재 중심 좌표를 가져오거나, 가져오지 못하는경우 대안 LatLng(Param)을 가져오는 확장함수
 */
fun KakaoMap.getCenterPositionOrElse(instead : LatLng) : LatLng = cameraPosition?.position ?: instead
