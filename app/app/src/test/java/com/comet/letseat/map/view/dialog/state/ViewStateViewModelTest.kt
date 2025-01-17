package com.comet.letseat.map.view.dialog.state

import android.util.Log
import com.ViewModelTest
import com.comet.letseat.common.view.state.ViewCheckState
import com.comet.letseat.common.view.state.toState
import com.comet.letseat.getOrAwaitValue
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
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

// 체크 박스의 상태를 관리하는 뷰모델 테스트 - 추상 클래스
class ViewStateViewModelTest : ViewModelTest() {

    // protected로 지정되어 있는 체크박스 상태
    lateinit var checkSelectionField : KProperty<*>

    // 다른 state 체크할때 사용되는 더미 뷰모델 - inital test 할때는 미사용
    lateinit var viewModel: ViewStateViewModel

    @Before
    fun before() {
        // 접근제한자 풀기
        // lazy로 지정되어 있으므로 다르게 설정됨
        checkSelectionField = ViewStateViewModel::class.memberProperties.find { it.name == "checkSelection" }!!.also {
            it.isAccessible = true
        }
        // empty state 제공
        viewModel = object : ViewStateViewModel() {
            override fun provideInitialSelection(): MutableList<ViewCheckState> {
                return mutableListOf()
            }
        }
        mockkStatic(Log::class)
    }

    @After
    fun tearDown() {
        checkSelectionField.isAccessible = false // 접근 제한자 정상화
        unmockkAll()
    }

    // 기본 체크박스 값을 가져오는지 확인
    @Test
    fun testProvideInitialSelection() {
        // initial 값 제공
        val initialList = mutableListOf("밥", "라면", "김치").toState()
        val viewModel = object : ViewStateViewModel() {
            override fun provideInitialSelection(): MutableList<ViewCheckState> {
                return initialList
            }
        }
        // 필드 확인
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertEquals(initialList, selection) // 최초값 일치한지

        // livedata도 최초 값으로 초기화되는지 확인
        kotlin.runCatching {
            viewModel.userSelectionLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            // 성공시
            assertEquals(initialList, it)
        }.onFailure {
            fail() // livedata를 가져오지 못했을때
        }
    }

    // add state method 테스트
    @Test
    fun testAddState() {
        val state = ViewCheckState("밥", true) // 체크된 밥 체크박스

        // 상태 추가
        val result = viewModel.addState(state)
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 필드에 추가 되었는지 확인
        assertTrue(result)
        assertTrue(selection.contains(state))
        // livedata 확인
        kotlin.runCatching {
            viewModel.userSelectionLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertEquals(selection, it)
        }.onFailure {
            fail() // 로드 실패시
        }
    }

    // add state method 실패 테스트
    @Test
    fun testAddState_Fail_With_Duplicate() {
        val state = ViewCheckState("밥", true) // 체크된 밥 체크박스
        val duplicate = ViewCheckState("밥", true) // 중복된 체크박스 이름
        // 상태 추가
        viewModel.addState(state)
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 중복 데이터 추가
        val result = viewModel.addState(duplicate)
        // 필드에 추가 안됐는지 확인
        assertFalse(result)
        assertEquals(1, selection.filter { it.data == state.data }.size)
    }


    // add all state method 테스트
    @Test
    fun testAddAllState() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스

        // 상태 추가
        val result = viewModel.addAllState(states)
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 필드에 추가 되었는지 확인
        assertTrue(selection.containsAll(states))
        // livedata 확인
        kotlin.runCatching {
            viewModel.userSelectionLiveData.getOrAwaitValue(1, TimeUnit.SECONDS)
        }.onSuccess {
            assertEquals(selection, it)
        }.onFailure {
            fail() // 로드 실패시
        }
        assertEquals(result, states) // 추가된 데이터가 입력값과 같은지
    }


    // add all state method 절반 실패 - 중복 데이터
    @Test
    fun testAddAllState_Half_Fail_With_Duplicate() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스
        val halfDuplicateState = listOf(ViewCheckState("밥", false), ViewCheckState("김치", true))
        // 상태 추가
        viewModel.addAllState(states)
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 중복 상태 추가
        val result = viewModel.addAllState(halfDuplicateState)

        assertTrue(result.isNotEmpty()) // 한개는 들어감
        assertEquals(1, result.size)
        assertEquals(states.size + result.size, selection.size) // 한개 추가됨
    }

    // add all state method 전부 실패 - 중복 데이터
    @Test
    fun testAddAllState_Fail_With_Duplicate() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스

        // 상태 추가
        viewModel.addAllState(states)
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 중복 상태 추가
        val result = viewModel.addAllState(states)

        assertTrue(result.isEmpty()) // 아무것도 안들어감
        assertEquals(states, selection) // 최초 주입 상태 - 중복 없는 상태 유지
    }


    // on check 성공시
    @Test
    fun testOnCheckSuccess() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스

        // 상태 추가
        viewModel.addAllState(states)

        // 체크 수행
        viewModel.onCheck(1) // 첫번째 항목 - 라면 체크, 반전되므로 true로 바뀜
        // livedata는 갱신되지 않음. 뷰가 스스로 체크 하므로. 또 클릭에서 갱신하면 recyclerview에서 화냄 - 왜 스크롤중에 갱신하냐고
        // reflection으로 가져오기
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        // 필드에 추가 되었는지 확인
        assertTrue(selection[1].isChecked)

        // 체크 수행
        viewModel.onCheck(0) // 두번째 항목 - 밥 체크, 반전되므로 false로 바뀜
        assertFalse(selection[0].isChecked)
    }

    // 뷰홀더가 초기화되지 않은 상황에서 -1 인덱스로 요청하는경우 무시됨
    @Test
    fun testOnCheckFailure_With_Index_Negative() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스

        // 로깅 mock
        every { Log.w(any(), any<String>()) } returns 0

        // 상태 추가
        viewModel.addAllState(states)

        // 체크 수행
        viewModel.onCheck(-1)

        // 바뀌지 않음
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertEquals(states, selection)

        verify(exactly = 1) { Log.w(any(), any<String>()) }
    }


    // 뷰홀더의 이상으로 범위를 벗어난경우 에러
    @Test
    fun testOnCheckFailure_With_Index_Out_Of_Range() {
        val states = listOf(ViewCheckState("밥", true), ViewCheckState("라면", false)) // 체크된 밥 체크박스

        // 로깅 mock
        every { Log.w(any(), any<String>()) } returns 0

        // 상태 추가
        viewModel.addAllState(states)

        // 체크 수행
        viewModel.onCheck(10)

        // 바뀌지 않음
        val selection = checkSelectionField.getter.call(viewModel) as MutableList<ViewCheckState>
        assertEquals(states, selection)

        verify(exactly = 1) { Log.w(any(), any<String>()) }
    }




}