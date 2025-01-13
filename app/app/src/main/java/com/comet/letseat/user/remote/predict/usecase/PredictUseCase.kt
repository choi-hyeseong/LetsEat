package com.comet.letseat.user.remote.predict.usecase

import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.comet.letseat.user.remote.predict.repository.PredictRepository
import com.skydoves.sandwich.ApiResponse
import java.util.UUID

/**
 * PredictRepository의 메뉴 추천을 수행하는 유스케이스
 * @see PredictRepository.predict
 * @property predictRepository 호출될 레포지토리입니다.
 */
class PredictUseCase(private val predictRepository: PredictRepository) {

    suspend operator fun invoke(uuid: UUID, categories : List<String>) : ApiResponse<PredictResponse> {
        return predictRepository.predict(uuid, categories)
    }
}