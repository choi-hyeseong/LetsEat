package com.comet.letseat.map.view.dialog.state

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.TAG
import com.comet.letseat.common.view.state.ViewCheckState

/**
 * ViewState를 활용하는 ViewModel의 추상 클래스. 해당 체크박스의 정보를 가져오고 recyclerView의 클릭 처리에 사용됨
 * @property checkSelection 사용자의 recycler view의 체크 정보를 담고 있음
 */
abstract class ViewStateViewModel : ViewModel() {

    // 유저의 체크박스 상태를 갖고 있는 필드
    protected val checkSelection: MutableList<ViewCheckState> by lazy { provideInitialSelection() }

    // 유저의 체크박스 정보를 제공할 live data
    val userSelectionLiveData: LiveData<List<ViewCheckState>>
        get() = _internalSelectionLiveData
    private val _internalSelectionLiveData: MutableLiveData<List<ViewCheckState>> = MutableLiveData(checkSelection)

    // 해당 위치의 체크박스 클릭시 데이터 업데이트
    fun onCheck(pos: Int) {
        // 범위 벗어난경우
        if (checkSelection.size <= pos || pos == -1) {
            Log.w(TAG, "result checkbox pos is invalid.")
            return
        }
        val checkState = checkSelection[pos]
        checkState.isChecked = !checkState.isChecked // 반전
    }

    /**
     * 동적으로 체크박스 추가 및 LiveData notify
     * @param state 추가할 체크박스의 정보입니다.
     */
    fun addState(state : ViewCheckState) {
        checkSelection.add(state)
        _internalSelectionLiveData.value = checkSelection
    }

    /**
     * 동적으로 체크박스 여러개 추가 및 LiveData notify
     * @param states 체크박스의 정보입니다.
     */
    fun addAllState(states : List<ViewCheckState>) {
        checkSelection.addAll(states)
        _internalSelectionLiveData.value = checkSelection
    }

    /**
     * 유저의 체크박스 데이터의 초기 값 제공
     */
    protected abstract fun provideInitialSelection() : MutableList<ViewCheckState>
}
