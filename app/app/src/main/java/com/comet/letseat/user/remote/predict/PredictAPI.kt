package com.comet.letseat.user.remote.predict

import com.comet.letseat.user.remote.predict.dto.PredictRequest
import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 카테고리를 통해 음식을 추천하는 API
 */
interface PredictAPI {

    @POST("/predict")
    suspend fun predict(@Body request : PredictRequest) : ApiResponse<PredictResponse>
}