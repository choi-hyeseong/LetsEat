package com.comet.letseat.map.view.dialog.choose

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.TAG
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidator

/**
 * 기존 Map ViewModel이 너무 무거워져서 dialog fragment에서 사용할 수 있는 뷰모델 생성 - 선택 화면
 */
class ChooseDialogViewModel(
        private val chooseInputValidator: ChooseInputValidator,
        private val predictValidator: PredictValidator
) : ViewModel() {

    companion object {
        // 유저 선택지의 기본 카테고리
        private val DEFAULT_CATEGORY: List<String> = listOf("아침", "점심", "저녁", "짠", "매운", "달달한", "신맛", "감칠맛")
    }

    // 유저의 선택지를 저장하고 있는 필드. 기본적으로 제공하는 카테고리 포함. 체크 상태도 포함
    private val userSelection: MutableList<ViewCheckState> = DEFAULT_CATEGORY.toMutableList()
        .toState()

    // 유저의 카테고리 정보를 제공할 live data
    val userSelectionLiveData: LiveData<List<ViewCheckState>>
        get() = _userCategoryLiveData
    private val _userCategoryLiveData: MutableLiveData<List<ViewCheckState>> = MutableLiveData<List<ViewCheckState>>(userSelection)


    // 유저 카테고리 입력 오류 검증 결과 반환 - 호출될일 없긴함.. view에서 검증하고 넘겨준거라
    val userInputErrorLiveData: LiveData<Event<ChooseValidErrorType>>
        get() = _userErrorNotifyLiveData

    // 내부적인 liveData
    private val _userErrorNotifyLiveData: MutableLiveData<Event<ChooseValidErrorType>> = MutableLiveData()

    // 유저 카테고리 선택 검증 오류 반환
    val userCheckboxErrorLiveData: LiveData<Event<PredictValidErrorType>>
        get() = _userCheckError

    private val _userCheckError: MutableLiveData<Event<PredictValidErrorType>> = MutableLiveData()


    /**
     * 사용자의 카테고리 직접 추가 메소드
     * @param category 입력값입니다.
     */
    fun addCategory(category: String) {
        // 검증
        val validateResult = chooseInputValidator.valid(category)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            if (error.isEmpty()) Log.w(TAG, "Validation is failed. But, error result is empty.")
            else _userErrorNotifyLiveData.value = Event(error.first().error)
            return
        }
        // 선택지 추가 및 알림
        userSelection.add(ViewCheckState(category, false))
        _userCategoryLiveData.value = userSelection
    }

    /**
     * Choose Dialog에서 체크박스 클릭하는경우 VM의 상태 변경
     * @param pos 클릭한 포지션입니다.
     */
    fun onChooseDialogCheck(pos: Int) {
        Log.w(TAG, userSelection.toString())
        if (userSelection.size <= pos || pos == -1) {
            Log.w(TAG, "checkbox pos is invalid.")
            return
        }
        val checkState = userSelection[pos]
        checkState.isChecked = !checkState.isChecked // 반전
    }


    /**
     * AI 음식 추천을 수행합니다.
     */
    fun predict() {
        // 검증
        val validateResult = predictValidator.validate(userSelection)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            if (error.isEmpty()) Log.w(TAG, "Predict Validation is failed. But, error result is empty.")
            else _userCheckError.value = Event(error.first().error)
            return
        }
        // 성공시 todo
    }

}