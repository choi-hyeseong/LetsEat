package com.comet.letseat.common.view.state

/**
 * domain / data 레이어가 아닌 뷰의 객체를 나타내기 위한 상태 클래스
 * @property data 담고 있는 데이터입니다.
 * @property isChecked 현재 해당 뷰가 체크되어 있는지 나타내는 값입니다.
 */
data class ViewCheckState(val data : String, var isChecked : Boolean)

/**
 * MutableList<String>으로 된 단순 값을 상태 클래스로 변환하는 확장함수
 */
fun MutableList<String>.toState() = this.map { ViewCheckState(it, false) }.toMutableList()