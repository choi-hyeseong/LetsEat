package com.comet.letseat.map.view.dialog.result

import com.ViewModelTest
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.convertToStringList
import com.comet.letseat.getOrAwaitValue
import com.comet.letseat.map.view.dialog.choose.ChooseDialogViewModel
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidErrorType
import com.comet.letseat.map.view.dialog.result.valid.result.ResultValidator
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

// 결과 다이얼로그 테스트
class ResultDialogViewModelTest : ViewModelTest() {

    // 테스트할 뷰모델
    lateinit var viewModel: ResultDialogViewModel

    // protected로 지정되어 있는 체크박스 상태
    lateinit var checkSelectionField: KProperty<*>

    @Before
    fun setUp() {
        // vm
        viewModel = ResultDialogViewModel(ResultValidator())
        // 접근제한자 풀기
        // lazy로 지정되어 있으므로 다르게 설정됨
        checkSelectionField = ChooseDialogViewModel::class.memberProperties.find { it.name == "checkSelection" }!!.also {
            it.isAccessible = true
        }
    }

    @After
    fun tearDown() {
        checkSelectionField.isAccessible = false
    }


    // 최초 체크박스 상태 확인
    @Test
    fun testInitialSelectionValue() {
        // 필드 확인
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertTrue(selection.isEmpty()) //비어 있는지 확인
    }

    // fragment argument로 받은 변수 입력 확인
    @Test
    fun testUpdateMenu() {
        val input = ResultDialogInput(mutableListOf("라면", "김치", "밥"))
        // 메뉴 업데이트
        viewModel.updateMenu(input)
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertEquals(selection.convertToStringList(), input.menus) // 입력값 일치한지 확인
    }

    // 검색 검증 성공시
    @Test
    fun testSearchSuccess() {
        val input = ResultDialogInput(mutableListOf("라면", "김치", "밥"))
        // 메뉴 업데이트
        viewModel.updateMenu(input)
        viewModel.onCheck(1) //첫번째꺼 클릭
        // 검색 수행
        viewModel.search()

        // 클릭된 정보
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>

        // livedata 검증
        kotlin.runCatching {
            viewModel.resultLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess { event ->
            assertFalse(event.isHandled)
            assertEquals(selection[1].data, event.getContent()) //클릭된 컨텐츠
        }.onFailure {
            fail()
        }
    }

    // 검색 검증 실패시
    @Test
    fun testSearchFailure() {
        val input = ResultDialogInput(mutableListOf("라면", "김치", "밥"))
        // 메뉴 업데이트
        viewModel.updateMenu(input)
        viewModel.onCheck(1) //첫번째꺼 클릭
        viewModel.onCheck(2) //두번째꺼 클릭
        // 검색 수행
        viewModel.search()


        // 결과 livedata 검증 - 업데이트 되선 안됨
        kotlin.runCatching {
            viewModel.resultLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            fail()
        }

        // error live data 검증
        kotlin.runCatching {
            viewModel.userResultSelectionErrorLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess { event ->
            assertFalse(event.isHandled)
            assertEquals(ResultValidErrorType.MULTIPLE, event.getContent())
        }.onFailure {
            fail()
        }
    }
}