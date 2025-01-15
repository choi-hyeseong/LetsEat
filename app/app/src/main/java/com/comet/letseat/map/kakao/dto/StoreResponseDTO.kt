package com.comet.letseat.map.kakao.dto

import com.comet.letseat.map.kakao.model.Store

/**
 * Kakao Map의 카테고리 기반 검색 결과
 */
data class StoreResponseDTO(
    val documents: List<Document>, // 가게 리스트
    val meta: Meta // 페이징 관련
)

data class Document(
    val address_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val category_name: String,
    val distance: String,
    val id: String,
    val phone: String,
    val place_name: String,
    val place_url: String,
    val road_address_name: String,
    val x: String,
    val y: String
)

data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val same_name: SameName,
    val total_count: Int
)

data class SameName(
    val keyword: String,
    val region: List<Any>,
    val selected_region: String
)

/**
 * Response DTO를 모델로 변경
 */
fun StoreResponseDTO.toModel() : List<Store> = documents.map { Store(it.place_name, it.phone, it.address_name, it.y.toDouble(), it.x.toDouble(), it.distance.toDouble()) }