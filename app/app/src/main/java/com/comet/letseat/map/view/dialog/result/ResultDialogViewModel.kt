package com.comet.letseat.map.view.dialog.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.TAG
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidErrorType
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator

class ResultDialogViewModel(private val validator: ResultValidator) : ViewModel() {

    // 추천받은 음식에 대한 유저의 선택지를 저장하고 있는 필드. 기본적으로 제공하는 카테고리 포함. 체크 상태도 포함
    private val resultSelection: MutableList<ViewCheckState> = mutableListOf("생선까스", "조기", "짜장면", "라면", "매운짬뽕").toState()

    // 유저의 추천받은 음식의 카테고리 정보를 제공할 live data
    val userResultLiveData: LiveData<List<ViewCheckState>>
        get() = _userResultSelectLiveData
    private val _userResultSelectLiveData: MutableLiveData<List<ViewCheckState>> = MutableLiveData<List<ViewCheckState>>(resultSelection)


    // 유저 결과 선택 검증 오류 반환
    val userResultSelectionErrorLiveData: LiveData<Event<ResultValidErrorType>>
        get() = _userSelectionError

    private val _userSelectionError: MutableLiveData<Event<ResultValidErrorType>> = MutableLiveData()

    fun onChooseResultSelection(pos: Int) {
        if (resultSelection.size <= pos || pos == -1) {
            Log.w(TAG, "result checkbox pos is invalid.")
            return
        }
        val checkState = resultSelection[pos]
        checkState.isChecked = !checkState.isChecked // 반전

    }


    /**
     * 가게 검색을 위해 selection한 result를 가져옵니다.
     */
    fun search() {
        // 검증
        val validateResult = validator.validate(resultSelection)
        // 검증 실패시
        if (!validateResult.isSuccess) {
            val error = validateResult.error
            if (error.isEmpty()) Log.w(TAG, "Result Validation is failed. But, error result is empty.")
            else _userSelectionError.value = Event(error.first().error)
            return
        }
        // 성공시 todo
    }
}