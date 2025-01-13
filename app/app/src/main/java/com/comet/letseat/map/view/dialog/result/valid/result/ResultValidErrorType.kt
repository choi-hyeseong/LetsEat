package com.comet.letseat.map.view.dialog.result.valid.result

/**
 * 결과 화면에서 선택지 에러 결과타입
 * @property EMPTY 아무것도 선택하지 않은경우 발생합니다.
 * @property MULTIPLE 두개 이상 선택된경우 발생합니다.
 */
enum class ResultValidErrorType {
    EMPTY, MULTIPLE
}