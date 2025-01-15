package com.comet.letseat.map.kakao.model

/**
 * DTO로 들어온 StoreResponseDTO를 가공해서 필요한 정보만 가공
 * @property name 장소의 이름
 * @property phone 전화번호
 * @property address 장소의 전화번호
 * @property latitude 위도
 * @property longitude 경도
 * @property distance 검색 중심 좌표로부터 거리.
 */
data class Store(val name: String, val phone: String, val address: String, val latitude: Double, val longitude: Double, val distance: Double)