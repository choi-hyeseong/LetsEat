package com.comet.letseat.common.util

import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtil {

    companion object {
        // todo move to hilt module

        private val BASE_URL = "http://0.tcp.jp.ngrok.io:14522/" // ngrok 이용한 임시 주소

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()


        fun <T> provideAPI(api: Class<T>): T {
            return retrofit.create(api)
        }
    }
}