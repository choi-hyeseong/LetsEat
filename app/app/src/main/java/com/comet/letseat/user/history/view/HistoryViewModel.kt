package com.comet.letseat.user.history.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.TAG
import com.comet.letseat.common.livedata.Event
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.type.NetworkErrorType
import com.comet.letseat.user.remote.user.model.UserHistory
import com.comet.letseat.user.remote.user.usecase.GetUserHistoryUseCase
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val loadUserUseCase: LoadUserUseCase,
                                           private val getUserHistoryUseCase: GetUserHistoryUseCase) : ViewModel() {

    private var cachedUser: UUID? = null // 추후에 초기화될 캐시된 유저 정보

    // 유저 히스토리 liveData
    val historyLiveData: LiveData<List<UserHistory>>
        get() = _innerHistory
    private val _innerHistory: MutableLiveData<List<UserHistory>> = MutableLiveData()

    // 에러 notify용
    val errorLiveData: LiveData<Event<NetworkErrorType>>
        get() = _innerError
    private val _innerError: MutableLiveData<Event<NetworkErrorType>> = MutableLiveData()

    // 유저 히스토리 로드
    fun loadHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            if (cachedUser == null)
                cachedUser = loadUserUseCase().uuid
            getUserHistoryUseCase(cachedUser!!).onSuccess {
                _innerHistory.postValue(data.histories)
            }.onError {
                // error post
                Log.w(TAG, "encountered history error ${errorBody?.string()}")
                _innerError.postValue(Event(NetworkErrorType.ERROR))
            }.onException {
                // exception post
                Log.e(TAG, "encountered history exception", exception)
                _innerError.postValue(Event(NetworkErrorType.EXCEPTION))
            }
        }
    }
}