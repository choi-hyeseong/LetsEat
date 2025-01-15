package com.comet.letseat.map.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.TAG
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.map.gps.model.UserLocation
import com.comet.letseat.map.gps.usecase.GetLocationUseCase
import com.comet.letseat.map.gps.usecase.GpsEnabledUseCase
import com.comet.letseat.map.kakao.model.Store
import com.comet.letseat.map.kakao.usecase.GetStoresByKeywordUseCase
import com.comet.letseat.map.view.type.GPSErrorType
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.predict.usecase.PredictUseCase
import com.comet.letseat.user.remote.type.NetworkErrorType
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 지도 클래스를 관리하는 VM
class MapViewModel(private val gpsEnabledUseCase: GpsEnabledUseCase,
                   private val getLocationUseCase: GetLocationUseCase,
                   private val loadUserUseCase: LoadUserUseCase,
                   private val predictUseCase: PredictUseCase,
                   private val getStoresByKeywordUseCase: GetStoresByKeywordUseCase) : ViewModel() {


    private var cachedUser: UserData? = null // 기존에 예측할때 필요한 유저 정보. 캐시시켜두고 여러번 사용할때 불러와서 사용하기

    // 액티비티에서 접근하는 LiveData. Mutable 하지 않게 제공
    val locationLiveData: LiveData<UserLocation>
        get() = _userLocationLiveData // get property이용해서 mutable한 livedata 제공
    private val _userLocationLiveData: MutableLiveData<UserLocation> = MutableLiveData() // lazy하게 load Location 걸었더니 kotlin property get이랑 겹쳐서 터짐..

    // 유저에게 gps 로드 요청시 결과값 나타낼 liveData. Event써서 1회성 notify만 하게
    val gpsResponseErrorLiveData: LiveData<Event<GPSErrorType>>
        get() = _gpsErrorLiveData
    private val _gpsErrorLiveData: MutableLiveData<Event<GPSErrorType>> = MutableLiveData()

    // 유저의 네트워크 요청 로딩 기다리기 위한 livedata - 1회성 팝업
    val loadingLiveData: LiveData<Event<Boolean>>
        get() = _networkLoadingLiveData
    private val _networkLoadingLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

    // 메뉴 추천 네트워크 요청 오류 알려주기 위한 liveData
    val predictNetworkErrorLiveData: LiveData<Event<NetworkErrorType>>
        get() = _predictNetworkError
    private val _predictNetworkError: MutableLiveData<Event<NetworkErrorType>> = MutableLiveData()

    // 메뉴 추천 결과 반환용
    val predictLiveData: LiveData<Event<List<String>>>
        get() = _predictResponseData
    private val _predictResponseData: MutableLiveData<Event<List<String>>> = MutableLiveData()

    // 가게 검색 api 결과 응답용
    val storeNetworkErrorLiveData: LiveData<Event<NetworkErrorType>>
        get() = _predictStoreNetworkError
    private val _predictStoreNetworkError: MutableLiveData<Event<NetworkErrorType>> = MutableLiveData()

    // 가게목록 liveData
    val storeLiveData : LiveData<List<Store>>
        get() = _localStoreLiveData
    private val _localStoreLiveData : MutableLiveData<List<Store>> = MutableLiveData()

    // gps 정보 불러오는 메소드
    fun loadLocation() {
        val isEnabled = gpsEnabledUseCase() // 활성화 여부
        if (!isEnabled) {
            // 활성화 되지 않은경우
            _gpsErrorLiveData.value = Event(GPSErrorType.NOT_ENABLED)
            return
        }
        getLocationUseCase().onSuccess {
            // 유저 정보 가져와지면
            _userLocationLiveData.value = it
        }.onFailure {
            // 실패시
            _gpsErrorLiveData.value = when (it) {
                is IllegalStateException -> Event(GPSErrorType.NOT_ENABLED) // gps 비활성화
                is NullPointerException -> Event(GPSErrorType.LOAD_FAIL) // 로드 실패
                else -> Event(GPSErrorType.INTERNAL) // 내부 오류 - 펄미션 없음 등
            }
        }
    }

    fun predict(categories: List<String>) {
        _networkLoadingLiveData.value = Event(true) // 로딩 보여주기
        // 비동기 처리
        CoroutineScope(Dispatchers.IO).launch {
            // 캐시된 유저가 없는경우
            if (cachedUser == null)
            // 유저 로드
                cachedUser = loadUserUseCase()
            predictUseCase(cachedUser!!.uuid, categories).onSuccess {
                _predictResponseData.postValue(Event(data.menus))
            }.onError {
                _predictNetworkError.postValue(Event(NetworkErrorType.EXCEPTION))
                Log.e(TAG, "encountered predict error : ${errorBody?.string()}")
            }.onException {
                _predictNetworkError.postValue(Event(NetworkErrorType.EXCEPTION))
                Log.e(TAG, "encountered predict exception", exception)
            }
            _networkLoadingLiveData.postValue(Event(false)) //로딩 끝내기
        }
    }

    // 키워드로 가게를 찾는 메소드
    fun findStores(x : Double, y : Double, keyword : String) {
        CoroutineScope(Dispatchers.IO).launch {
            getStoresByKeywordUseCase(keyword, x, y).onSuccess {
                _localStoreLiveData.postValue(it)
                Log.e(TAG, it.toString())
            }.onFailure {
                Log.e(TAG, it.message, it)
                _predictStoreNetworkError.postValue(Event(NetworkErrorType.ERROR))
            }
        }
    }


}