package com.comet.letseat

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LetsEatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 다크모드 사용 안하게
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}