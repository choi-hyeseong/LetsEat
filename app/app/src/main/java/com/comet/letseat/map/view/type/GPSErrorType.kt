package com.comet.letseat.map.view.type

/**
 * GPS 데이터 로드 실패시 뷰에게 알려줄 type
 * @property NOT_ENABLED GPS가 켜져있지 않음
 * @property LOAD_FAIL GPS 주소를 불러오는데 실패
 * @property INTERNAL 내부적인 오류 - GPS 권한 회수등
 */
enum class GPSErrorType {
    NOT_ENABLED, LOAD_FAIL, INTERNAL
}