package com.comet.letseat.map.kakao.repoisotry

import com.comet.letseat.map.kakao.api.KakaoAPI
import com.comet.letseat.map.kakao.dto.toModel
import com.comet.letseat.map.kakao.model.Store
import com.skydoves.sandwich.getOrThrow

/**
 * 카카오맵을 사용하는 검색 api 레포지토리
 */
class KakaoMapRepository(private val kakaoAPI: KakaoAPI) : MapRepository {

    /**
     * runCatching으로 잡아서 Result 형태로 반환
     */
    override suspend fun findStoresByKeyword(query: String, x: String, y: String): Result<List<Store>> {
        return kotlin.runCatching {
            val response = kakaoAPI.findStoresByKeyword(query, x, y).getOrThrow() // api 호출. 실패시 throw 해서 failure 호출되게
            response.toModel() // 모델로 변경
        }
    }
}