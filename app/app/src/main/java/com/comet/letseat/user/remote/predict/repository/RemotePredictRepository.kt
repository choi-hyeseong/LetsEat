package com.comet.letseat.user.remote.predict.repository

import com.comet.letseat.user.remote.predict.PredictAPI
import com.comet.letseat.user.remote.predict.dto.PredictRequest
import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.skydoves.sandwich.ApiResponse
import java.util.UUID

/**
 * Retrofit을 이용한 PredictRespository의 구현체입니다.
 * @property predictAPI 예측에 수행되는 retrofit 인터페이스입니다.
 */
class RemotePredictRepository(private val predictAPI: PredictAPI) : PredictRepository {
    override suspend fun predict(uuid: UUID, categories: List<String>): ApiResponse<PredictResponse> {
        return predictAPI.predict(PredictRequest(uuid, categories))
    }

}