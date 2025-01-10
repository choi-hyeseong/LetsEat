package com.comet.letseat.common.livedata

/**
 * toast와 같은 1회성 이벤트를 livedata에 넣을경우 화면 회전등과 같이 갱신시 지속적으로 호출되는 문제를 해결하기 위한 클래스 입니다.
 * @property content Toast등 1회성으로만 호출될 객체입니다.
 */
open class Event<T>(private val content : T) {

    //이미 한번 처리된건지 확인하는 변수
    var isHandled : Boolean = false
        private set

    /**
     * 내용물을 1회 한정해 가져오는 메소드 입니다. 만약 최초로 내용물을 가져오게 된다면 isHandled 변수에 의해 다음부터는 가져올 수 없습니다.
     * @return T? 이미 가져온 데이터의 경우 null로 반환됩니다.
     */
    fun getContent() : T? {
        return if (isHandled) null
        else {
            isHandled = true
            content
        }
    }

    /**
     * 내용물을 강제로 가져오는 메소드입니다.
     * @return T 내용물입니다.
     */
    fun getContentForce() : T = content
}