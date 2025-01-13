package com.comet.letseat.user.remote.predict.repository

import com.comet.letseat.user.remote.predict.dto.PredictResponse
import com.skydoves.sandwich.ApiResponse
import java.util.UUID

/**
 * 사용자의 카테고리 입력을 통해 메뉴를 추천하는 레포지토르
 */
interface PredictRepository {

    /**
     * 카테고리를 바탕으로 음식 추천을 수행합니다.
     * @param uuid 사용자의 uuid입니다.
     * @param categories 입력된 카테고리 입니다.
     */
    suspend fun predict(uuid : UUID, categories : List<String>) : ApiResponse<PredictResponse>
}