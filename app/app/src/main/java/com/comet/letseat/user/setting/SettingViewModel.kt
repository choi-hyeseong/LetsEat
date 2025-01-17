package com.comet.letseat.user.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.remote.user.usecase.RemoteDeleteUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 유저 정보 삭제용 뷰모델
@HiltViewModel
class SettingViewModel @Inject constructor(private val loadUserUseCase: LoadUserUseCase,
                                           private val deleteUserUseCase: DeleteUserUseCase,
                                           private val remoteDeleteUserUseCase: RemoteDeleteUserUseCase) : ViewModel() {

    val deleteResponseLiveData: LiveData<Boolean>
        get() = _innerResponse
    private val _innerResponse: MutableLiveData<Boolean> = MutableLiveData()

    // 뷰모델 스코프를 사용함으로써 뒤로가기시 cancel 되게.
    // 뒤로가기를 하면 로컬 정보는 남아있지만, 서버 정보는 삭제 된 상태. 하지만 로컬 정보가 남아있어도 문제가 크게 되지는 않음.
    // 하지만, CoroutineScope를 열어서 처리한다면 delete까지 진행되기 때문에 로컬 uuid가 날아가 exception이 발생함
    // 따라서 로컬 데이터의 정합성이 깨지더라도 큰 effect가 없기 때문에(서버의 정보가 없는정도 = 새로운 유저랑 다를게 없음) viewModelScope를 사용
    fun deleteUser() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                // 사용자 정보 안전하게 가져오기
                val uuid = kotlin.runCatching {
                    loadUserUseCase().uuid
                }.getOrNull()

                if (uuid == null) {
                    _innerResponse.postValue(false)
                    return@withContext
                }

                // 원격지 정보 삭제하기
                val isSuccess = remoteDeleteUserUseCase(uuid)

                // 성공 실패시
                if (!isSuccess) {
                    _innerResponse.postValue(false)
                    return@withContext
                }
                // 성공시 로컬 정보 삭제
                deleteUserUseCase()
                _innerResponse.postValue(true)

            }
        }
    }
}