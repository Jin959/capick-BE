package com.capick.capick.controller;

import com.capick.capick.domain.cafe.CafeTheme;
import com.capick.capick.domain.cafe.CafeType;
import com.capick.capick.dto.response.LocationResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.service.CafeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CafeController.class)
class CafeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CafeService cafeService;

    @Test
    @DisplayName("성공: 지도 서비스상의 ID로 카페를 조회한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void getCafeByMapVendorPlaceId() throws Exception {
        // given
        CafeResponse response = CafeResponse.builder()
                .location(LocationResponse.builder().build())
                .cafeType(CafeType.NONE)
                .cafeTheme(CafeTheme.NORMAL)
                .build();
        when(cafeService.getCafeByMapVendorPlaceId(anyString())).thenReturn(response);
        String requestKakaoPlaceId = "1234567";

        // when // then
        mockMvc.perform(
                        get("/api/cafes/kakao/{placeId}", requestKakaoPlaceId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.location").exists())
                .andExpect(jsonPath("$.data.cafeType").exists())
                .andExpect(jsonPath("$.data.cafeTheme").exists())
                .andDo(print());
    }

}