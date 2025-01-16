package com.comet.letseat.common.util

import com.comet.letseat.BuildConfig
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtil {

    companion object {
        // todo move to hilt module

        private val BASE_URL = BuildConfig.SERVER_URL
        private val KAKAO_BASE_URL = "https://dapi.kakao.com/v2/local/"

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()

        //카카오 전용 api 클라
        val kakaoClient : OkHttpClient = OkHttpClient.Builder().addInterceptor(NetworkInterceptor()).build()
        val kakaoRetrofit : Retrofit = Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .client(kakaoClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()

        fun <T> provideAPI(api: Class<T>): T {
            return retrofit.create(api)
        }

        fun <T> provideKakaoAPI(api : Class<T>) : T {
            return kakaoRetrofit.create(api)
        }

    }

    // 헤더에 인증 정보 담기 위한 인터셉터
   class NetworkInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json;charset=UTF-8 ")
                .addHeader("Authorization", "KakaoAK ${BuildConfig.WEB_API_KEY}")
                .build()
            return chain.proceed(request)
        }
    }
}