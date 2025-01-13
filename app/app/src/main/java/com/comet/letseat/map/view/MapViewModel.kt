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
import com.comet.letseat.map.view.dialog.choose.valid.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.ChooseValidError
import com.comet.letseat.map.view.type.GPSErrorType

// 지도 클래스를 관리하는 VM
class MapViewModel(private val gpsEnabledUseCase: GpsEnabledUseCase, private val getLocationUseCase: GetLocationUseCase, private val chooseInputValidator: ChooseInputValidator) : ViewModel() {


    companion object {
        // 유저 선택지의 기본 카테고리
        private val DEFAULT_CATEGORY : List<String> = listOf("아침", "점심", "저녁", "짠", "매운", "달달한", "신맛", "감칠맛")
    }

    // 액티비티에서 접근하는 LiveData. Mutable 하지 않게 제공
    val locationLiveData : LiveData<UserLocation>
        get() = _userLocationLiveData // get property이용해서 mutable한 livedata 제공

    // 실질적으로 내부에서 관리되는 LiveData. Mutable함
    private val _userLocationLiveData : MutableLiveData<UserLocation> = MutableLiveData() // lazy하게 load Location 걸었더니 kotlin property get이랑 겹쳐서 터짐..

    // 유저에게 gps 로드 요청시 결과값 나타낼 liveData. Event써서 1회성 notify만 하게
    val gpsResponseErrorLiveData : LiveData<Event<GPSErrorType>>
        get() = _gpsErrorLiveData

    private val _gpsErrorLiveData : MutableLiveData<Event<GPSErrorType>> = MutableLiveData()

    // 유저의 카테고리 정보를 제공할 live data
    val userSelectionLiveData : LiveData<List<String>>
        get() = _userCategoryLiveData

    // 유저의 선택지를 저장하고 있는 필드. 기본적으로 제공하는 카테고리 포함
    private val userSelection : MutableList<String> = mutableListOf<String>().apply { addAll(DEFAULT_CATEGORY) }
    private val _userCategoryLiveData : MutableLiveData<List<String>> = MutableLiveData<List<String>>().apply { value = userSelection }

    // 유저 카테고리 입력 오류 검증 결과 반환 - 호출될일 없긴함.. view에서 검증하고 넘겨준거라
    val userSelectionErrorLiveData : LiveData<Event<ChooseValidError>>
        get() = _userErrorNotifyLiveData
    // 내부적인 liveData
    private val _userErrorNotifyLiveData : MutableLiveData<Event<ChooseValidError>> = MutableLiveData()


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

    /**
     * 사용자의 카테고리 직접 추가 메소드
     * @param category 입력값입니다.
     */
    fun addCategory(category : String) {
        // 검증
        val validateResult = chooseInputValidator.valid(category)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            if (error.isEmpty())
                Log.w(TAG, "Validation is failed. But, error result is empty.")
            else
                _userErrorNotifyLiveData.value = Event(error.first().error)
            return
        }
        // 선택지 추가 및 알림
        userSelection.add(category)
        _userCategoryLiveData.value = userSelection
    }
}