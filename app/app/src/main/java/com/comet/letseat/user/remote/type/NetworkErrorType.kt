package com.comet.letseat.user.remote.type

/**
 * 뷰에 나타낼 네트워크 오류 타입
 * @property ERROR 내부에서 처리하던 중 오류가 발생했습니다. error body가 존재합니다.
 * @property EXCEPTION 오류로써 핸들링 할 수 없는 문제가 발생했습니다. error body대신 exception이 존재합니다.
 */
enum class NetworkErrorType {
    ERROR, EXCEPTION
}