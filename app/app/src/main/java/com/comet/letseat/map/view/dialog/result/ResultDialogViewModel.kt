package com.comet.letseat.map.view.dialog.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comet.letseat.TAG
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.common.view.state.toStringMap
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidErrorType
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator
import com.comet.letseat.map.view.dialog.state.ViewStateViewModel

class ResultDialogViewModel(private val validator: ResultValidator) : ViewStateViewModel() {

    // 유저 결과 선택 검증 오류 반환
    val userResultSelectionErrorLiveData: LiveData<Event<ResultValidErrorType>>
        get() = _userSelectionError

    private val _userSelectionError: MutableLiveData<Event<ResultValidErrorType>> = MutableLiveData()

    // 최종 선택 결과 설정용 observer
    val resultLiveData : LiveData<Event<String>>
        get() = _userResultLiveData
    private val _userResultLiveData : MutableLiveData<Event<String>> = MutableLiveData()


    /**
     * 가게 검색을 위해 selection한 result를 가져옵니다.
     */
    fun search() {
        // 검증
        val validateResult = validator.validate(checkSelection)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            if (error.isEmpty()) Log.w(TAG, "Result Validation is failed. But, error result is empty.")
            else _userSelectionError.value = Event(error.first().error)
            return
        }
        // 1개만 선택됐으므로 맨 처음 item 가져옴
        _userResultLiveData.value = Event(checkSelection.filter { it.isChecked }.toStringMap().first())
    }

    /**
     * argument로 받은 데이터 필드에 할당
     */
    fun updateMenu(input : ResultDialogInput) {
        checkSelection.addAll(input.menus.toState())
    }

    override fun provideInitialSelection(): MutableList<ViewCheckState> {
        return mutableListOf() // 결과 값은 fragment argument로 받으므로 mutable한 empty list 반환
    }
}