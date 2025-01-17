package com.comet.letseat.module

import android.content.Context
import com.comet.letseat.common.storage.LocalStorage
import com.comet.letseat.common.storage.PreferenceDataStore
import com.comet.letseat.map.gps.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext context : Context) : LocalStorage {
        return PreferenceDataStore(context)
    }

    @Provides
    @Singleton
    fun provideLocationDao(@ApplicationContext context : Context) : LocationDao {
        return LocationDao(context)
    }
}