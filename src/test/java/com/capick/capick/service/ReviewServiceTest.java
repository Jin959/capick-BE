package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.MemberRepository;
import com.capick.capick.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.capick.capick.domain.cafe.CafeType.COST_EFFECTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        cafeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 회원은 까페 후기를 남기고 공유하기 위해 리뷰를 생성할 수 있다.")
    void createReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3);

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequest);

        // then
        assertThat(response.getId()).isNotNull();
        // TODO: 등록 시간에 대해서 테스트하는 데 문제가 있다. 테스트를 위해 reviewService.createReview 에서 파라미터로 등록 시간을 받아야할까?
        assertThat(response)
                .extracting("writer.id", "writer.nickname", "visitPurpose", "content", "menu", "createdAt")
                .contains(writerId, "nickname01", "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", LocalDateTime.now());
    }

    @Test
    @DisplayName("성공: 어떤 까페에 리뷰가 처음 작성되는 경우, 까페가 등록되면서 까페 타입이 정해진다.")
    void createFirstReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");

        ReviewCreateRequest reviewCreateRequestForCostEffectiveTypeCafe
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3);

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequestForCostEffectiveTypeCafe);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("name", "kakaoPlaceId", "kakaoDetailPageUrl", "cafeType")
                .contains(
                        tuple("스타벅스 광화문점", "1234567", "https://place.url", COST_EFFECTIVE)
                );

    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

    private static CafeCreateRequest createCafeCreateRequest(
            String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return CafeCreateRequest.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

    private static ReviewCreateRequest createReviewCreateRequest(
            Long writerId, CafeCreateRequest cafe, String visitPurpose, String content,
            String menu, int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex) {
        return ReviewCreateRequest.builder()
                .writerId(writerId)
                .cafe(cafe)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .build();
    }

}