package com.comet.letseat.map.view.dialog.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.convertToStringList
import com.comet.letseat.common.view.state.toState
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
            _userSelectionError.value = Event(error.first().error)
            return
        }
        // 1개만 선택됐으므로 맨 처음 item 가져옴
        _userResultLiveData.value = Event(checkSelection.filter { it.isChecked }.convertToStringList().first())
    }

    /**
     * argument로 받은 데이터 필드에 할당
     */
    fun updateMenu(input : ResultDialogInput) {
        // addAll만 수행했는데 LiveData에 notify가 된 상황. 관련해서 검색해도 자료가 나오질 않고, 내부 코드 봐도 잘 모르겠음.
        // 보통 List갱신 후 notify를 하는게 맞는데..
        // thread로 테스트 해본결과 지속적으로 적용되는건 아닌듯 함. 따라서 observe 시점에서 value에 대한 참조를 갖고 있어서 생기는 문제가 아닌가 싶음.
        // checkSelection.addAll(input.menus.toState())
        addAllState(input.menus.toState()) // 명시적으로 추가하기 (setValue)
    }

    override fun provideInitialSelection(): MutableList<ViewCheckState> {
        return mutableListOf() // 결과 값은 fragment argument로 받으므로 mutable한 empty list 반환
    }
}