package com.comet.letseat.module

import com.comet.letseat.common.storage.LocalStorage
import com.comet.letseat.map.gps.dao.LocationDao
import com.comet.letseat.map.gps.repository.LocationRepository
import com.comet.letseat.map.gps.repository.NetworkLocationRepository
import com.comet.letseat.map.kakao.api.KakaoAPI
import com.comet.letseat.map.kakao.repoisotry.KakaoMapRepository
import com.comet.letseat.map.kakao.repoisotry.MapRepository
import com.comet.letseat.user.local.repository.PreferenceUserRepository
import com.comet.letseat.user.local.repository.UserRepository
import com.comet.letseat.user.remote.predict.PredictAPI
import com.comet.letseat.user.remote.predict.repository.PredictRepository
import com.comet.letseat.user.remote.predict.repository.RemotePredictRepository
import com.comet.letseat.user.remote.user.api.UserAPI
import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import com.comet.letseat.user.remote.user.repository.RetrofitRemoteUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideLocationRepository(dao : LocationDao) : LocationRepository {
        return NetworkLocationRepository(dao)
    }

    @Provides
    @Singleton
    fun provideMapRepository(kakaoAPI: KakaoAPI) : MapRepository {
        return KakaoMapRepository(kakaoAPI)
    }

    @Provides
    @Singleton
    fun provideUserRepository(localStorage: LocalStorage) : UserRepository {
        return PreferenceUserRepository(localStorage)
    }

    @Provides
    @Singleton
    fun providePredictRepository(predictAPI: PredictAPI) : PredictRepository {
        return RemotePredictRepository(predictAPI)
    }

    @Provides
    @Singleton
    fun provideRemoteUserRepository(userAPI: UserAPI) : RemoteUserRepository {
        return RetrofitRemoteUserRepository(userAPI)
    }
}