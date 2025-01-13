package com.comet.letseat.common.dialog

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.comet.letseat.notifyMessage

/**
 * 커스텀 다이얼로그의 추상 클래스
 */
abstract class AbstractDialog<T : ViewBinding> : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bind = provideBinding(inflater, container, savedInstanceState)
        initView(bind)
        return bind.root
    }

    /**
     * 토스트 생성용 함수
     * @see MainActivity.notifyMessage
     */
    protected fun notifyMessage(resId : Int) {
        requireActivity().notifyMessage(resId)
    }


    /**
     * 토스트 생성용 함수
     * @see MainActivity.notifyMessage
     */
    protected fun notifyMessage(message : String) {
        requireActivity().notifyMessage(message)
    }

    // 뷰 초기화 함수
    protected abstract fun initView(bind: T)

    // create시 뷰 바인딩을 제공할 함수
    protected abstract fun provideBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : T

    /**
     * 커스텀 다이얼로그의 크기 조절 함수
     * @param width 퍼센트 단위 너비입니다. 0.9 = 90%
     * @param height 퍼센트 단위 높이 입니다. 0.9 = 90%
     */
    protected open fun resize(width: Float, height: Float) {
        val windowManager = getSystemService(requireContext(), WindowManager::class.java) as WindowManager
        // x 좌표
        val x: Int
        // y 좌표
        val y: Int
        // 30 이전 버젼인경우
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            x = (size.x * width).toInt()
            y = (size.y * height).toInt()
        }
        else {
            val rect = windowManager.currentWindowMetrics.bounds

            x = (rect.width() * width).toInt()
            y = (rect.height() * height).toInt()

        }
        val window = dialog!!.window
        window?.setLayout(x, y)
    }
}