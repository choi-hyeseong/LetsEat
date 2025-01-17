package com

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

/**
 * 뷰모델 테스트를 하는 클래스들이 상속받아야 할 테스트 클래스
 * LiveData 테스트를 위해선 Rule Field가 필요한데, 이를 상속만 하면 자동적으로 따라오게 설정
 */
abstract class ViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()
}