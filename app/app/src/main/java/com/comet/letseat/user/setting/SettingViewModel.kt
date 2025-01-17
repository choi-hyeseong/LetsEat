package com.comet.letseat.user.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.user.usecase.RemoteDeleteUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 유저 정보 삭제용 뷰모델
class SettingViewModel(private val loadUserUseCase: LoadUserUseCase, private val deleteUserUseCase: DeleteUserUseCase, private val remoteDeleteUserUseCase: RemoteDeleteUserUseCase) : ViewModel() {

    val deleteResponseLiveData : LiveData<Boolean>
        get() = _innerResponse
    private val _innerResponse : MutableLiveData<Boolean> = MutableLiveData()


    fun deleteUser() {
        CoroutineScope(Dispatchers.IO).launch {
            // 사용자 정보 안전하게 가져오기
            val uuid = kotlin.runCatching {
                loadUserUseCase().uuid
            }.getOrNull()

            if (uuid == null) {
                _innerResponse.postValue(false)
                return@launch
            }

            // 원격지 정보 삭제하기
            val isSuccess = remoteDeleteUserUseCase(uuid)

            // 성공 실패시
            if (!isSuccess) {
                _innerResponse.postValue(false)
                return@launch
            }
            // 성공시 로컬 정보 삭제
            deleteUserUseCase()
            _innerResponse.postValue(true)

        }
    }
}