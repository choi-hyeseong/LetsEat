package com.comet.letseat.map.kakao.usecase

import com.comet.letseat.map.kakao.model.Store
import com.comet.letseat.map.kakao.repoisotry.MapRepository

/**
 * Map Repository 참조해서 키워드 검색하는 유스케이스
 * @property repository 참조하는 지도 레포지토리입니다.
 */
class GetStoresByKeywordUseCase(private val repository: MapRepository) {

    /**
     * @see MapRepository.findStoresByKeyword
     */
    suspend operator fun invoke(query : String, x : String, y : String) : Result<List<Store>> {
        return repository.findStoresByKeyword(query, x, y)
    }

}