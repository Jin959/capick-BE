package com.capick.capick.controller;

import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("성공: 회원은 등록된 까페에 대해 리뷰를 생성할 수 있다. HTTP 상태 코드 200 및 자체 응답 코드 201을 반환한다.")
    void createReview() throws Exception {
        // given
        ReviewResponse response = ReviewResponse.builder()
                .writer(MemberSimpleResponse.builder().build())
                .imageUrls(List.of())
                .build();

        when(reviewService.createReview(any(ReviewCreateRequest.class), any(LocalDateTime.class))).thenReturn(response);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of("image1.url", "http://image2.url", "https://이미지3.url/이미지?=mage%201.jpeg"))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("리소스 생성에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.writer").exists())
                .andExpect(jsonPath("$.data.imageUrls").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 작성자 회원 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutWriterId() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of("https://image1.url", "https://image2.url", "https://image3.url"))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성자의 회원 리소스 아이디를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 외부 지도 서비스의 대상 까페에 대한 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafe() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("외부 지도 서비스의 대상 카페에 대한 리소스 아이디를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 카페 방문 목적은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutVisitPurpose() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성을 위해 카페를 방문한 목적을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("경계: 리뷰 생성 시 방문 목적은 최대 20자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithVisitPurposeLengthOutOfRange() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요".repeat(20))
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("카페 방문 목적은 최대 20자입니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 리뷰 내용은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithEmptyContent() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 내용을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("경계: 리뷰 생성 시 리뷰 내용은 최대 300자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithContentLengthOutOfRange() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용".repeat(300))
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 내용은 최대 300자입니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 메뉴는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutMenu() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰할 메뉴를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 설문을 통한 까페 타입 지수는 필수값이다. 하나라도 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafeTypeIndex() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰하기 위해 공간에 대한 질문에 응답해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 까페 테마는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafeTheme() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰하기 위해 컨셉이나 테마에 대한 질문에 응답해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    // TODO: Parameterized 가능한가
    @Test
    @DisplayName("예외: 리뷰 생성 시 리뷰 이미지는 URL 형태이고 프로토콜은 HTTP, HTTPS 를 사용해야 한다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithInvalidImageUrls() throws Exception {
        // given
        ReviewCreateRequest request1 = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of("ftp://image2.url"))
                .build();
        ReviewCreateRequest request2 = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://place.url")
                                .build()
                )
                .visitPurpose("일하거나 책읽기 좋아요")
                .content("리뷰 내용")
                .menu("아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of("https://이미지3.url/    공백"))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

}