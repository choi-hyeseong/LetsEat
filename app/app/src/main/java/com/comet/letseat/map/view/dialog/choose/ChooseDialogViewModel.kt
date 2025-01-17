package com.comet.letseat.map.view.dialog.choose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.convertToStringList
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidator
import com.comet.letseat.map.view.dialog.state.ViewStateViewModel

/**
 * 기존 Map ViewModel이 너무 무거워져서 dialog fragment에서 사용할 수 있는 뷰모델 생성 - 선택 화면
 */
class ChooseDialogViewModel(
        private val chooseInputValidator: ChooseInputValidator,
        private val predictValidator: PredictValidator
) : ViewStateViewModel() {

    companion object {
        // 유저 선택지의 기본 카테고리
        private val DEFAULT_CATEGORY: List<String> = listOf("아침", "점심", "저녁", "짠", "매운", "달달한", "신맛", "감칠맛")
    }


    // 유저 카테고리 입력 오류 검증 결과 반환 - 호출될일 없긴함.. view에서 검증하고 넘겨준거라
    val userInputErrorLiveData: LiveData<Event<ChooseValidErrorType>>
        get() = _userErrorNotifyLiveData
    private val _userErrorNotifyLiveData: MutableLiveData<Event<ChooseValidErrorType>> = MutableLiveData()

    // 유저 카테고리 선택 검증 오류 반환
    val userCheckboxErrorLiveData: LiveData<Event<PredictValidErrorType>>
        get() = _userCheckError
    private val _userCheckError: MutableLiveData<Event<PredictValidErrorType>> = MutableLiveData()


    // 최종 선택 결과 설정용 observer
    val resultLiveData : LiveData<Event<List<String>>>
        get() = _userResultLiveData
    private val _userResultLiveData : MutableLiveData<Event<List<String>>> = MutableLiveData()


    override fun provideInitialSelection(): MutableList<ViewCheckState> {
        return DEFAULT_CATEGORY.toMutableList().toState()
    }

    /**
     * 사용자의 카테고리 직접 추가 메소드
     * @param category 입력값입니다.
     */
    fun addCategory(category: String) {
        // 검증
        val validateResult = chooseInputValidator.valid(checkSelection, category)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            _userErrorNotifyLiveData.value = Event(error.first().error)
            return
        }
        // 선택지 추가 및 알림
        addState(ViewCheckState(category, false))
    }

    /**
     * AI 음식 추천을 수행합니다.
     */
    fun predict() {
        // 검증
        val validateResult = predictValidator.validate(checkSelection)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            _userCheckError.value = Event(error.first().error)
            return
        }
        _userResultLiveData.value = Event(checkSelection.filter { it.isChecked }.convertToStringList())
    }

}