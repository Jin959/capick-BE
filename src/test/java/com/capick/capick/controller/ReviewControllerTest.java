package com.capick.capick.controller;

import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.request.ReviewUpdateRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("성공: 리뷰를 생성한다. HTTP 상태 코드 200 및 자체 응답 코드 201을 반환한다.")
    void createReview() throws Exception {
        // given
        ReviewSimpleResponse response = ReviewSimpleResponse.builder()
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
                .imageUrls(List.of(
                        "https://storage.com/images/80459",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                ))
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
                .imageUrls(List.of(
                        "https://storage.com/images/80459",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                ))
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
    @DisplayName("예외: 리뷰 생성 시 대상 카페에 대한 정보는 일부 필수값이다. 카페 자체를 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafe() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
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
                .andExpect(jsonPath("$.message").value("리뷰를 작성할 카페의 정보를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 대상 카페 이름은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafeName() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name(" ")
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
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/reviews/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("카페 이름을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 대상 카페에 대한 외부 지도 서비스의 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithoutCafePlaceId() throws Exception {
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
                .andExpect(jsonPath("$.message").value("카페에 대한 외부 지도 서비스 상의 리소스 아이디를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 대상 카페에 대한 상세 페이지는 HTTP 또는 HTTPS 프로토콜을 사용한 URL 이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithInvalidCafeDetailPageUrl() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://pl     ace.url")
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
                .andExpect(jsonPath("$.message").value("카페 상세 페이지에 허용되지 않는 URL 입니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 생성 시 대상 카페에 대한 상세 페이지 URL은 최대 50자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createReviewWithCafeDetailPageUrlLengthOutOfRange() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .cafe(
                        CafeCreateRequest.builder()
                                .name("스타벅스 광화문점")
                                .kakaoPlaceId("1234567")
                                .kakaoDetailPageUrl("https://" + "place.".repeat(50) + "url")
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
                .andExpect(jsonPath("$.message").value("카페 상세 페이지 URL은 50자가 넘을 수 없습니다."))
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
    void createReviewWithoutContent() throws Exception {
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
    @DisplayName("예외: 리뷰 생성 시 메뉴는 필수값이며 공백을 허용하지 않는다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
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
                .menu(" ")
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
    @DisplayName("예외: 리뷰 생성 시 설문을 통한 카페 타입 지수는 필수값이다. 하나라도 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
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
    @DisplayName("예외: 리뷰 생성 시 카페 테마는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
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

    @Test
    @DisplayName("성공: 단 건의 리뷰를 조회한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void getReview() throws Exception {
        // given
        ReviewSimpleResponse response = ReviewSimpleResponse.builder()
                .writer(MemberSimpleResponse.builder().build())
                .imageUrls(List.of())
                .build();
        when(reviewService.getReview(anyLong())).thenReturn(response);
        int requestReviewId = 123;

        // when // then
        mockMvc.perform(
                        get("/api/reviews/{reviewId}", requestReviewId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.writer").exists())
                .andExpect(jsonPath("$.data.imageUrls").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 단 건의 리뷰를 상세 조회한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void getReviewDetail() throws Exception {
        // given
        ReviewResponse response = ReviewResponse.builder()
                .writer(MemberSimpleResponse.builder().build())
                .imageUrls(List.of())
                .build();
        when(reviewService.getReviewDetail(anyLong())).thenReturn(response);
        int requestReviewId = 123;

        // when // then
        mockMvc.perform(
                        get("/api/reviews/{reviewId}/detail", requestReviewId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.writer").exists())
                .andExpect(jsonPath("$.data.imageUrls").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 리뷰를 수정한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void updateReview() throws Exception {
        // given
        ReviewSimpleResponse response = ReviewSimpleResponse.builder()
                .writer(MemberSimpleResponse.builder().build())
                .imageUrls(List.of())
                .build();

        when(reviewService.updateReview(anyLong(), any(ReviewUpdateRequest.class))).thenReturn(response);

        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of(
                        "https://storage.com/images/80459",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                ))
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.writer").exists())
                .andExpect(jsonPath("$.data.imageUrls").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 작성자 회원 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void reviewUpdateWithoutWriterId() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
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
    @DisplayName("예외: 리뷰 수정 시 카페 방문 목적은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void reviewUpdateWithoutVisitPurpose() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 수정을 위해 카페를 방문한 목적을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("경계: 리뷰 수정 시 방문 목적은 최대 20자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithVisitPurposeLengthOutOfRange() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요".repeat(20))
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
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
    @DisplayName("예외: 리뷰 수정 시 리뷰 내용은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithoutContent() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 수정을 위해 리뷰 내용을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("경계: 리뷰 수정 시 리뷰 내용은 최대 300자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithContentLengthOutOfRange() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용".repeat(300))
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
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
    @DisplayName("예외: 리뷰 수정 시 메뉴는 필수값이며 공백을 허용하지 않는다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithoutMenu() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("    ")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 수정을 위해 리뷰할 메뉴를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 설문을 통한 카페 타입 지수는 필수값이다. 하나라도 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithoutCafeTypeIndex() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 수정을 위해 커피 맛에 대한 질문에 응답해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 카페 테마는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithoutCafeTheme() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 수정을 위해 컨셉이나 테마에 대한 질문에 응답해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    // TODO: Parameterized 가능한지 생각하고 적용해보기
    @Test
    @DisplayName("예외: 리뷰 수정 시 리뷰 이미지는 URL 형태이고 프로토콜은 HTTP, HTTPS 를 사용해야 한다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateReviewWithInvalidImageUrls() throws Exception {
        // given
        int requestReviewId = 123;
        ReviewCreateRequest request1 = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of(
                        "ftp://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                ))
                .build();
        ReviewCreateRequest request2 = ReviewCreateRequest.builder()
                .writerId(1L)
                .visitPurpose("일하거나 책읽고 공부하려고요")
                .content("리뷰 내용")
                .menu("아이스 아메리카노")
                .coffeeIndex(3)
                .spaceIndex(3)
                .priceIndex(3)
                .noiseIndex(3)
                .theme("normal")
                .imageUrls(List.of(
                        "https://storage.com/images%2F     공백불가pathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                ))
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
        mockMvc.perform(
                        patch("/api/reviews/{reviewId}", requestReviewId)
                                .content(objectMapper.writeValueAsString(request2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 리뷰를 삭제한다. HTTP 상태 코드 200 및 자체 응답 코드 204 를 반환한다.")
    void deleteReview() throws Exception {
        // given
        int reviewId = 123;

        // when // then
        mockMvc.perform(
                        delete("/api/reviews/{reviewId}", reviewId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.message").value("리소스 삭제에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

}