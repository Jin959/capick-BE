package com.capick.capick.repository;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Test
    @DisplayName("성공: 삭제되지 않았거나 작성된 리뷰를 조회할 수 있다.")
    void findByIdAndStatus() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Review review = createReview("넓어서 갔어요", "리뷰 내용", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt);
        reviewRepository.save(review);

        // when
        Optional<Review> optionalReview = reviewRepository.findByIdAndStatus(review.getId(), ACTIVE);

        // then
        assertThat(optionalReview).isPresent();
        assertThat(optionalReview.get()).usingRecursiveComparison().isEqualTo(review);
    }

    @Test
    @DisplayName("성공: 삭제되지 않았거나 작성된 리뷰와 해당 리뷰를 작성한 회원을 조회한다.")
    void findWithMemberByIdAndStatus() {
        // given
        Member writer = createMember("email01@naver.com", "password01", "member1");
        memberRepository.save(writer);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Review review = createReview(writer, "넓어서 갔어요", "리뷰 내용", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt);
        reviewRepository.save(review);

        // when
        Optional<Review> optionalReviewWithMember = reviewRepository.findWithMemberByIdAndStatus(review.getId(), ACTIVE);

        // then
        assertThat(optionalReviewWithMember).isPresent();
        Review reviewWithMember = optionalReviewWithMember.get();
        assertThat(reviewWithMember)
                .usingRecursiveComparison()
                .ignoringFields("writer")
                .isEqualTo(review);
        assertThat(reviewWithMember.getWriter())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(writer);
    }

    @Test
    @DisplayName("성공: 특정 카페에 대해 작성된 리뷰를 페이징 처리하여 조회한다.")
    void findPageByCafeAndStatus() {
        // given
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        cafeRepository.save(cafe);

        List<Review> reviews = List.of(
                createReview(
                        cafe, "넓어서 갔어요", "리뷰 내용1", "핫 아메리카노", 3, 3, 4, 3, "normal",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                createReview(
                        cafe, "넓어서 갔어요", "리뷰 내용2", "핫 아메리카노", 3, 3, 4, 3, "normal",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                createReview(
                        cafe, "넓어서 갔어요", "리뷰 내용3", "핫 아메리카노", 3, 3, 4, 3, "normal",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                createReview(
                        cafe, "넓어서 갔어요", "리뷰 내용4", "핫 아메리카노", 3, 3, 4, 3, "normal",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                createReview(
                        cafe, "넓어서 갔어요", "리뷰 내용5", "핫 아메리카노", 3, 3, 4, 3, "normal",
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        );
        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));

        // when
        Page<Review> reviewPage = reviewRepository.findPageByCafeAndStatus(cafe, ACTIVE, pageRequest);

        // then
        assertThat(reviewPage.getTotalPages()).isEqualTo(2);
        assertThat(reviewPage.getTotalElements()).isEqualTo(5L);
        assertThat(reviewPage.getContent()).hasSize(3)
                .extracting("id", "content", "status", "cafe.name", "cafe.kakaoPlaceId")
                .containsExactly(
                        tuple(savedReviews.get(4).getId(), "리뷰 내용5", ACTIVE, "스타벅스 광화문점", "1234567"),
                        tuple(savedReviews.get(3).getId(), "리뷰 내용4", ACTIVE, "스타벅스 광화문점", "1234567"),
                        tuple(savedReviews.get(2).getId(), "리뷰 내용3", ACTIVE, "스타벅스 광화문점", "1234567")
                );

    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private Review createReview(
            String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
        return Review.builder()
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .theme(theme)
                .registeredAt(registeredAt)
                .build();
    }

    private Review createReview(
            Member writer, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
        return Review.builder()
                .writer(writer)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .theme(theme)
                .registeredAt(registeredAt)
                .build();
    }

    private Review createReview(
            Cafe cafe, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
        return Review.builder()
                .cafe(cafe)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .theme(theme)
                .registeredAt(registeredAt)
                .build();
    }

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

}