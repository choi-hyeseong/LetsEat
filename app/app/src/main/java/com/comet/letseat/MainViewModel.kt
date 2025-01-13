package com.comet.letseat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.letseat.user.local.model.UserData
import com.comet.letseat.user.local.usecase.ExistUserUseCase
import com.comet.letseat.user.local.usecase.SaveUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 맨 처음 앱 실행시 유저 정보를 초기화하는 뷰모델
 * @property existUserUseCase 유저 데이터의 존재 여부를 확인합니다.
 * @property saveUserUseCase 유저 데이터가 저장되어 있지 않은경우 랜덤하게 생성해서 저장할때 사용됩니다.
 */
class MainViewModel(private val existUserUseCase: ExistUserUseCase, private val saveUserUseCase: SaveUserUseCase) : ViewModel() {

    // init시 사용되는 liveData. boolean값이 true면 초기화 됨
    val initializeLiveData : LiveData<Boolean>
        get() = _initLiveData
    private val _initLiveData : MutableLiveData<Boolean> = MutableLiveData()

    fun initUserData() {
       CoroutineScope(Dispatchers.IO).launch {
           if (!existUserUseCase())
               saveUserUseCase(UserData(UUID.randomUUID())) //random uuid save
           _initLiveData.postValue(true)
       }
    }

}