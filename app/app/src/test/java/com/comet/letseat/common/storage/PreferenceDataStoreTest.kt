package com.comet.letseat.common.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.lang.reflect.InvocationTargetException

// datastore test
@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceDataStoreTest {

    // context mock
    private lateinit var context: Context

    // datastore mock
    private lateinit var dataStore: DataStore<Preferences>

    // temp folder
    @Rule
    @JvmField
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    // test dispatcher - datastore
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    @Before
    fun beforeInit() {
        context = mockk()
        dataStore = PreferenceDataStoreFactory.create(scope = testScope, produceFile = { tmpFolder.newFile("eat.preferences_pb") }) // test data store - extension must be .preferences_pb
        mockkStatic(PreferenceDataStore::class.java.canonicalName!!.plus("Kt")) // extension fun mock
        every { context.dataStore } returns dataStore
    }

    @After
    fun afterTest() {
        unmockkAll()
    }

    // test have key
    @Test
    fun testHasKey() = runTest {
        val key = "INPUT_KEY"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        assertTrue(preferenceDataStore.hasKey(key))
    }

    // test does not have key
    @Test
    fun testDoesNotHaveKey() = runTest {
        val key = "INPUT_KEY"
        val preferenceDataStore = PreferenceDataStore(context)
        assertFalse(preferenceDataStore.hasKey(key))
    }

    // test key delete
    @Test
    fun testDelete() = runTest {
        val key = "INPUT_KEY"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        assertTrue(preferenceDataStore.hasKey(key)) // put int 이후 success
        preferenceDataStore.delete(key) // 키 삭제
        assertFalse(preferenceDataStore.hasKey(key))
    }


    /**
     * 이전에 저장된 데이터가 없는 경우 테스트 - init block 미고려
     */

    /*
    * INT PART
     */

    // put int success
    @Test
    fun testPutIntSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        val data = dataStore.data.map { it[intPreferencesKey(key)] }.firstOrNull()
        // 이거 테스트하면서 알게 됐는데, stringPreferencesKey로 생성된 Preference.Key 객체는 equals 비교할때 내부 name (string key값)만 비교함
        // 따라서 unique한 객체간 비교 (메모리 주소 비교)가 아님. 따라서 그냥 생성해서 써도 되긴 할듯.
        assertEquals(value, data)
    }

    // put int override (다른 데이터가 저장된곳에 덮어쓰기 테스트)
    @Test
    fun testPutIntOverrideSuccess() = runTest {
        val key = "INPUT_KEY"
        val previous = "STRING"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putString(key, previous)
        preferenceDataStore.putInt(key, value)
        val data = dataStore.data.map { it[intPreferencesKey(key)] }.firstOrNull()
        assertEquals(value, data)
    }

    // get int success
    @Test
    fun testGetIntSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        val result = preferenceDataStore.getInt(key, -1)
        assertEquals(value, result)
        assertNotEquals(-1, result)
    }

    // failure with access empty preference storage
    @Test
    fun testGetIntFailureWithEmpty() = runTest {
        val key = "INPUT_KEY"
        val value = 3
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getInt(key, -1)
        assertNotEquals(value, result)
        assertEquals(-1, result)
    }

    // failure with access wrong type area - string형 공간에 int로 가져오기
    @Test
    fun testGetIntFailureWithWrongType() = runTest {
        val key = "INPUT_KEY"
        val value = "VALUE"
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putString(key, value)
        val result = preferenceDataStore.getInt(key, -1)
        assertEquals(-1, result)
    }

    /*
    * STRING PART
    */


    // put string sucess
    @Test
    fun testPutStringSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = "SUCCESS"
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putString(key, value)
        val data = dataStore.data.map { it[stringPreferencesKey(key)] }.firstOrNull()
        assertEquals(value, data)
    }

    // put string override (다른 데이터가 저장된곳에 덮어쓰기 테스트)
    @Test
    fun testPutStringOverrideSuccess() = runTest {
        val key = "INPUT_KEY"
        val previous = 3
        val value = "STRING"
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, previous)
        preferenceDataStore.putString(key, value)
        val data = dataStore.data.map { it[stringPreferencesKey(key)] }.firstOrNull()
        assertEquals(value, data)
    }

    // get string success
    @Test
    fun testGetStringSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = "RESULT"
        val instead = "null"
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putString(key, value)
        val result = preferenceDataStore.getString(key, instead)
        assertEquals(value, result)
        assertNotEquals(instead, result)
    }

    // failure with access empty preference storage
    @Test
    fun testGetStringFailureWithEmpty() = runTest {
        val key = "INPUT_KEY"
        val value = "RESULT"
        val instead = "null"
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getString(key, instead)
        assertNotEquals(value, result)
        assertEquals(instead, result)
    }

    // failure with access wrong type area - int형 공간에 string으로 가져오기
    @Test
    fun testGetStringFailureWithWrongType() = runTest {
        val key = "INPUT_KEY"
        val value = 1
        val instead = "VALUE"
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        val result = preferenceDataStore.getString(key, instead)
        assertEquals(instead, result)
    }

    /*
    * BOOLEAN PART
     */

    @Test
    fun testPutBooleanSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = false
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putBoolean(key, value)
        val data = dataStore.data.map { it[booleanPreferencesKey(key)] }.firstOrNull()
        assertEquals(value, data)
    }

    // put boolean override (다른 데이터가 저장된곳에 덮어쓰기 테스트)
    @Test
    fun testPutBooleanOverrideSuccess() = runTest {
        val key = "INPUT_KEY"
        val previous = 3
        val value = false
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, previous)
        preferenceDataStore.putBoolean(key, value)
        val data = dataStore.data.map { it[booleanPreferencesKey(key)] }.firstOrNull()
        assertEquals(value, data)
    }

    // get boolean success
    @Test
    fun testGetBooleanSuccess() = runTest {
        val key = "INPUT_KEY"
        val value = true
        val instead = false
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putBoolean(key, value)
        val result = preferenceDataStore.getBoolean(key, instead)
        assertEquals(value, result)
        assertNotEquals(instead, result)
    }

    // failure with access empty preference storage
    @Test
    fun testGetBooleanFailureWithEmpty() = runTest {
        val key = "INPUT_KEY"
        val value = true
        val instead = false
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getBoolean(key, instead)
        assertNotEquals(value, result)
        assertEquals(instead, result)
    }

    // failure with access wrong type area - int형 공간에 string으로 가져오기
    @Test
    fun testGetBooleanFailureWithWrongType() = runTest {
        val key = "INPUT_KEY"
        val value = 1
        val instead = false
        val preferenceDataStore = PreferenceDataStore(context)
        preferenceDataStore.putInt(key, value)
        val result = preferenceDataStore.getBoolean(key, instead)
        assertEquals(instead, result)
    }

    /**
     * 앱이 재시동되어 map 구성 되는것 확인
     */

    // 기존 데이터가 존재해 map이 구성된경우
    @Test
    @Suppress("UNCHECKED_CAST") //for reflection
    fun testReInitSuccess() = runTest {
        // first pref
        val firstKey = "String_KEY"
        val firstValue = "STRING"
        // second pref
        val secondKey = "Int_KEY"
        val secondValue = 3
        // 키 2개가 기존에 존재하는 상황
        dataStore.edit { it[stringPreferencesKey(firstKey)] = firstValue }
        dataStore.edit { it[intPreferencesKey(secondKey)] = secondValue }

        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)

        val field = PreferenceDataStore::class.java.getDeclaredField("globalKeyMap")
            .also { it.isAccessible = true } // reflection을 이용해서 접근
        val keyMap: Map<String, Preferences.Key<*>> = field.get(preferenceDataStore) as Map<String, Preferences.Key<*>> // 해당 map은 key가 타입 소거됨
        // 키 가져왔는지
        assertEquals(2, keyMap.size)
        // 키 포함하는지 확인
        assertTrue(keyMap.keys.contains(firstKey))
        assertTrue(keyMap.keys.contains(secondKey))
        field.isAccessible = false
    }

    /**
     *  ReInit이후 String이 올바르게 가져와지는 경우 / 못가져오는경우(잘못된 타입 공간 접근) 테스트
     */

    @Test
    fun testReInitGetStringSuccess() = runTest {
        // first pref
        val firstKey = "String_KEY"
        val firstValue = "STRING"
        val instead = "null"
        // 1개의 키 존재
        dataStore.edit { it[stringPreferencesKey(firstKey)] = firstValue }

        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getString(firstKey, instead)
        assertEquals(firstValue, result)
        assertNotEquals(instead, result)
    }

    @Test
    fun testReInitGetStringFailure() = runTest {
        val instead = "null"
        // wrong memory area
        val firstKey = "Int_KEY"
        val firstValue = 3
        dataStore.edit { it[intPreferencesKey(firstKey)] = firstValue }

        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getString(firstKey, instead)
        assertEquals(instead, result)
    }

    /**
     *  ReInit이후 Int가 올바르게 가져와지는 경우 / 못가져오는경우(잘못된 타입 공간 접근) 테스트
     */

    @Test
    fun testReInitGetIntSuccess() = runTest {
        // first pref
        val firstKey = "Int_KEY"
        val firstValue = 1
        val instead = -1
        // 1개의 키 존재
        dataStore.edit { it[intPreferencesKey(firstKey)] = firstValue }

        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getInt(firstKey, instead)
        assertEquals(firstValue, result)
        assertNotEquals(instead, result)
    }

    @Test
    fun testReInitGetIntFailure() = runTest {
        val instead = -1
        // wrong memory area
        val firstKey = "String_KEY"
        val firstValue = "RESULT"
        dataStore.edit { it[stringPreferencesKey(firstKey)] = firstValue }

        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)
        val result = preferenceDataStore.getInt(firstKey, instead)
        assertEquals(instead, result)
    }

    // 기존 데이터가 없어 map이 구성되지 않은경우 (맨 위 테스트코드의 상황과 동일)
    @Test
    @Suppress("UNCHECKED_CAST") //for reflection
    fun testReInitFail() = runTest {
        // init되는 시점
        val preferenceDataStore = PreferenceDataStore(context)

        val field = PreferenceDataStore::class.java.getDeclaredField("globalKeyMap")
            .also { it.isAccessible = true } // reflection을 이용해서 접근
        val keyMap: Map<String, Preferences.Key<*>> = field.get(preferenceDataStore) as Map<String, Preferences.Key<*>> // 해당 map은 key가 타입 소거됨
        // 키 가져왔는지
        assertTrue(keyMap.isEmpty())
        field.isAccessible = false
    }

    /**
     * private providePreferenceKey 테스트
     */

    @Test
    @Suppress("UNCHECKED_CAST") // 여러 타입 지원하므로 캐스팅 수행
    fun testProvidePreferenceKey() {
        val preferenceDataStore = PreferenceDataStore(context)
        // get declared method로 가져올때 param도 선언해줘야 함
        val method = PreferenceDataStore::class.java.getDeclaredMethod("providePreferenceKey", String::class.java, Any::class.java)
            .also { it.isAccessible = true } // reflection을 이용해서 접근
        // 제네릭타입은 검증하기 까다로움. 따라서 PreferenceKey만 적절하게 제공하는지 확인하고, 미지원시 throw 되는것 확인

        // int
        val intKey = "INT_KEY"
        val intValue = 10
        val intResult = method.invoke(preferenceDataStore, intKey, intValue) as? Preferences.Key<Int>
        assertNotNull(intResult)
        assertEquals(intKey, intResult?.name)

        // string
        val stringKey = "INT_KEY"
        val stringValue = 10
        val stringResult = method.invoke(preferenceDataStore, stringKey, stringValue) as? Preferences.Key<String>
        assertNotNull(stringResult)
        assertEquals(stringKey, stringResult?.name)

        // string
        val booleanKey = "INT_KEY"
        val booleanValue = 10
        val booleanResult = method.invoke(preferenceDataStore, booleanKey, booleanValue) as? Preferences.Key<Boolean>
        assertNotNull(booleanResult)
        assertEquals(booleanKey, booleanResult?.name)

        // double
        val doubleKey = "DOUBLE_KEY"
        val doubleValue = 10.0
        val doubleResult = method.invoke(preferenceDataStore, doubleKey, doubleValue) as? Preferences.Key<Double>
        assertNotNull(doubleResult)
        assertEquals(doubleKey, doubleResult?.name)

        // float
        val floatKey = "FLOAT_KEY"
        val floatValue = 10.0f
        val floatResult = method.invoke(preferenceDataStore, floatKey, floatValue) as? Preferences.Key<Float>
        assertNotNull(floatResult)
        assertEquals(floatKey, floatResult?.name)

        // long
        val longKey = "LONG_KEY"
        val longValue = 10L
        val longResult = method.invoke(preferenceDataStore, longKey, longValue) as? Preferences.Key<Long>
        assertNotNull(longResult)
        assertEquals(longKey, longResult?.name)

        // exception - not support
        val listKey = "LIST_KEY"
        val listValue = mutableListOf<String>()
        // list not support
        val exception = assertThrows(InvocationTargetException::class.java) { method.invoke(preferenceDataStore, listKey, listValue) as? Preferences.Key<List<String>> } //reflection이므로 ITE발생
        assertEquals(IllegalArgumentException::class.java, exception.targetException::class.java)
    }
}