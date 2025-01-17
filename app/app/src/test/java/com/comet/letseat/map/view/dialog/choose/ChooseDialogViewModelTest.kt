package com.comet.letseat.map.view.dialog.choose

import com.ViewModelTest
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.getOrAwaitValue
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseInputValidator
import com.comet.letseat.map.view.dialog.choose.valid.choose.ChooseValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidErrorType
import com.comet.letseat.map.view.dialog.choose.valid.predict.PredictValidator
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class ChooseDialogViewModelTest : ViewModelTest() {

    companion object {
        // 유저 선택지의 기본 카테고리
        private val DEFAULT_CATEGORY: List<String> = listOf("아침", "점심", "저녁", "짠", "매운", "달달한", "신맛", "감칠맛")
    }

    // protected로 지정되어 있는 체크박스 상태
    lateinit var checkSelectionField : KProperty<*>

    // viewmodel
    lateinit var viewModel: ChooseDialogViewModel

    @Before
    fun setUp() {
        // 접근제한자 풀기
        // lazy로 지정되어 있으므로 다르게 설정됨
        checkSelectionField = ChooseDialogViewModel::class.memberProperties.find { it.name == "checkSelection" }!!.also {
            it.isAccessible = true
        }
        // vm 설정
        viewModel = ChooseDialogViewModel(ChooseInputValidator(), PredictValidator())
    }

    @After
    fun tearDown() {
        checkSelectionField.isAccessible = false // 접근제한자 복구
        unmockkAll()
    }

    // 최초 체크박스 상태 확인
    @Test
    fun testInitialSelectionValue() {
        // 필드 확인
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertTrue(selection.isNotEmpty()) //비어 있지 않음
        assertEquals(DEFAULT_CATEGORY.toState(), selection) // 기본 값이 동일한지
    }

    // add category 성공시
    @Test
    fun testAddCategorySuccess() {
        viewModel.addCategory("라면")
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertTrue(selection.contains(ViewCheckState("라면", false)))
    }

    // add category 실패시
    @Test
    fun testAddCategoryFailure() {
        viewModel.addCategory("라면") // 주입 성공
        viewModel.addCategory("라면") // 주입 실패
        // livedata 갱신 확인
        kotlin.runCatching {
            viewModel.userInputErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess { event ->
            assertFalse(event.isHandled)
            assertEquals(ChooseValidErrorType.DUPLICATE, event.getContent()) // 중복된 데이터 알림
        }.onFailure {
            fail()
        }
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertEquals(DEFAULT_CATEGORY.size + 1, selection.size) // 사이즈 일치 확인 - 기본 카테고리 + 라면 1개 추가한 사이즈 되야함
    }

    @Test
    fun testPredictSuccess() {
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        viewModel.onCheck(1) // 2번째 위치에 있는거 클릭 - 최초 클릭이므로 하나만 true로 됨
        viewModel.onCheck(4) // 5번재 위치에 있는거 클릭 - 얘도 true
        viewModel.predict() // 선택지 가져옴
        // 결과 확인
        kotlin.runCatching {
            viewModel.resultLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess { event ->
            assertFalse(event.isHandled)
            // 데이터 일치한 지 확인
            val result = event.getContent()!!
            assertEquals(2, result.size)
            assertEquals(selection[1].data, result[0])
            assertEquals(selection[4].data, result[1])
        }.onFailure {
            fail()
        }
    }

    @Test
    fun testPredictFailure() {
        // 아무것도 클릭 안함
        viewModel.predict() // 선택지 가져옴
        // 결과 확인
        kotlin.runCatching {
            viewModel.userCheckboxErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertFalse(it.isHandled)
            assertEquals(PredictValidErrorType.EMPTY, it.getContent())
        }.onFailure {
            fail()
        }

        // 결과 livedata는 갱신되지 말아야함
        kotlin.runCatching {
            viewModel.resultLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }
    }

}