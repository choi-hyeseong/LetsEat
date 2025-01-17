package com.comet.letseat.module

import com.comet.letseat.map.gps.repository.LocationRepository
import com.comet.letseat.map.gps.usecase.GetLocationUseCase
import com.comet.letseat.map.gps.usecase.GpsEnabledUseCase
import com.comet.letseat.map.kakao.repoisotry.MapRepository
import com.comet.letseat.map.kakao.usecase.GetStoresByKeywordUseCase
import com.comet.letseat.user.local.repository.UserRepository
import com.comet.letseat.user.local.usecase.DeleteUserUseCase
import com.comet.letseat.user.local.usecase.ExistUserUseCase
import com.comet.letseat.user.local.usecase.LoadUserUseCase
import com.comet.letseat.user.local.usecase.SaveUserUseCase
import com.comet.letseat.user.remote.predict.repository.PredictRepository
import com.comet.letseat.user.remote.predict.usecase.PredictUseCase
import com.comet.letseat.user.remote.user.repository.RemoteUserRepository
import com.comet.letseat.user.remote.user.usecase.GetUserHistoryUseCase
import com.comet.letseat.user.remote.user.usecase.RemoteDeleteUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserHistoryUseCase(repository: RemoteUserRepository) : GetUserHistoryUseCase {
        return GetUserHistoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoteDeleteUserUseCase(repository: RemoteUserRepository) : RemoteDeleteUserUseCase {
        return RemoteDeleteUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun providePredictUseCase(repository: PredictRepository) : PredictUseCase {
        return PredictUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteUserUseCase(repository: UserRepository) : DeleteUserUseCase {
        return DeleteUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideExistUserUseCase(repository: UserRepository) : ExistUserUseCase {
        return ExistUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoadUserUseCase(repository: UserRepository) : LoadUserUseCase {
        return LoadUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveUserUseCase(repository: UserRepository) : SaveUserUseCase {
        return SaveUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStoresByKeywordUseCase(repository: MapRepository) : GetStoresByKeywordUseCase {
        return GetStoresByKeywordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLocationUseCase(repository : LocationRepository) : GetLocationUseCase {
        return GetLocationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGpsEnabledUseCase(repository: LocationRepository) : GpsEnabledUseCase {
        return GpsEnabledUseCase(repository)
    }
}