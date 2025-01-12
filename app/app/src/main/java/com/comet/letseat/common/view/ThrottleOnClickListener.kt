package com.comet.letseat.common.view

import android.view.View

/**
 * 뷰의 중복 클릭을 방지하기 위한 쓰로틀링 클릭 리스너 크랠스
 * @property throttleMillis 클릭을 제한할 시간입니다. ms
 * @property clickFunction 클릭시 수행될 함수입니다.
 */
class ThrottleOnClickListener(private val throttleMillis : Long, private val clickFunction : (View) -> Unit) : View.OnClickListener {

    private var lastClickMillis : Long = 0

    /**
     * throttleMillis를 받지 않는 생성자 입니다. throttleMillis가 300ms로 지정됩니다.
     */
    constructor(clickFunction: (View) -> Unit) : this(300L, clickFunction)

    override fun onClick(v: View) {
        //현재시간과 마지막 클릭시간의 차이가 쓰로틀보다 클경우
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickMillis >= throttleMillis) {
            lastClickMillis = currentTime //최종 클릭시간 업데이트
            clickFunction(v) //수행
        }
    }

}

/**
 * View의 확장함수로 제작하여 바로 적용할 수 있게한 함수입니다. ThrottleMillis를 지정할 수 있습니다.
 */
fun View.setThrottleClickListener(throttleMillis: Long, clickFunction: (View) -> Unit) {
    setOnClickListener(ThrottleOnClickListener(throttleMillis, clickFunction))
}

/**
 * View의 확장함수로 제작하여 바로 적용할 수 있게한 함수입니다. ThrottleMillis가 기본값인 300ms로 지정됩니다.
 */
fun View.setThrottleClickListener(clickFunction: (View) -> Unit) {
    setOnClickListener(ThrottleOnClickListener(clickFunction))
}