package com.comet.letseat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * LiveData 테스팅용 확장함수. observerForever를 호출하기 때문에 Main Dispatcher에서 수행 되어야 합니다! (Unit Test에서는 필요 X)
 * @param time 해당 LiveData의 값을 읽어올 시간입니다.
 * @param timeUnit 해당 LiveData의 값을 읽어올 시간 단위입니다.
 */
fun <T : Any> LiveData<T>.getOrAwaitValue(
        time: Long, timeUnit: TimeUnit
): T {
    var data : T? = null //결과값
    val latch = CountDownLatch(1) //Async Latch
    val observer = object : Observer<T> {
        // 값 변경 감지되면 Latch 내려서 await 종료
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            removeObserver(this) //옵저버도 제거
        }
    }
    observeForever(observer) //계속 대기

    //래치가 시간동안 기다려도 안되면 Exception
    if (!latch.await(time, timeUnit))
        throw TimeoutException("LiveData값이 지정되지 않았습니다.")

    return data as T
}