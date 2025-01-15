package com.comet.letseat.map.kakao.api

import com.comet.letseat.map.kakao.dto.StoreResponseDTO
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Kakao Map의 API 호출하는 Retrofit API
 */
interface KakaoAPI {

    /**
     * 키워드 기반으로 가게를 찾습니다.
     * @param query 검색할 키워드입니다.
     * @param x 중심 x 좌표입니다.
     * @param y 중심 y 좌표입니다.
     */
    @GET("search/keyword.json")
    suspend fun findStoresByKeyword(@Query("query") query : String,@Query("x") x : String,@Query("y") y : String) : ApiResponse<StoreResponseDTO>

}