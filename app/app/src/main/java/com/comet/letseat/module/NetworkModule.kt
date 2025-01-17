package com.comet.letseat.module

import com.comet.letseat.BuildConfig
import com.comet.letseat.map.kakao.api.KakaoAPI
import com.comet.letseat.user.remote.predict.PredictAPI
import com.comet.letseat.user.remote.user.api.UserAPI
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        private val KAKAO_BASE_URL = "https://dapi.kakao.com/v2/local/"
    }


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoQualifier // 카카오 retrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LambdaQualifier // 람다 retrofit


    @Provides
    @Singleton
    fun provideKakaoClient() : OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(NetworkInterceptor()).build()
    }

    @Provides
    @Singleton
    @KakaoQualifier
    fun provideKakaoRetrofit(client : OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @LambdaQualifier
    fun provideLambdaRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePredictAPI(@LambdaQualifier retrofit: Retrofit) : PredictAPI {
        return retrofit.create(PredictAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideUserAPI(@LambdaQualifier retrofit: Retrofit) : UserAPI {
        return retrofit.create(UserAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideKakaoAPI(@KakaoQualifier retrofit: Retrofit) : KakaoAPI {
        return retrofit.create(KakaoAPI::class.java)
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