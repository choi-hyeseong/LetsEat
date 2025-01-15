package com.comet.letseat.map.kakao.dto

import org.junit.Assert.assertEquals
import org.junit.Test

// DTO -> Model 변환 테스트
class StoreResponseDTOTest {

    @Test
    fun testConvertDTOToModel() {
        val documents = mutableListOf(
            // 첫번째 검색 결과
            Document(
                address_name = "경북 구미시",
                category_group_code = "CODE",
                category_group_name = "GROUP_NAME",
                category_name = "검색한 카테고리",
                distance = "500",
                id = "id",
                phone = "010-1234-5678",
                place_name = "스토어",
                place_url = "URL",
                road_address_name = "무슨무슨길",
                x = "117.5",
                y = "45.0"
            ),
            // 두번째 검색 결과
            Document(
                address_name = "경북 김천시",
                category_group_code = "CODE",
                category_group_name = "GROUP_NAME",
                category_name = "검색한 카테고리",
                distance = "520",
                id = "id",
                phone = "010-1234-5678",
                place_name = "스토어",
                place_url = "URL",
                road_address_name = "무슨무슨길",
                x = "1127.5",
                y = "445.0"
            ))
        // dto 값
        val dto = StoreResponseDTO(documents, Meta(false, 0, SameName("", mutableListOf(), ""), 0))
        // 모델로 변환한 값
        val models = dto.toModel()

        // 검증
        assertEquals(2, models.size)
        models.forEachIndexed { index, model ->
            val origin = documents[index] // expected data
            assertEquals(origin.place_name, model.name)
            assertEquals(origin.y.toDouble(), model.latitude, 0.0)
            assertEquals(origin.x.toDouble(), model.longitude, 0.0)
            assertEquals(origin.phone, model.phone)
            assertEquals(origin.address_name, model.address)
            assertEquals(origin.distance.toDouble(), model.distance, 0.0)

        }
    }
}