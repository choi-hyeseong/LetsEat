package com.comet.letseat.map.kakao.repoisotry

import com.comet.letseat.map.kakao.model.Store

/**
 * 맵 관련 api 호출 레포지토리
 */
interface MapRepository {

    /**
     * 키워드를 기반으로 가게를 검색합니다.
     * @param query 검색할 키워드
     * @param x 검색할 중심 x 좌표
     * @param y 검색할 중심 y 좌표
     */
    suspend fun findStoresByKeyword(query : String, x : String, y : String) : Result<List<Store>>
}