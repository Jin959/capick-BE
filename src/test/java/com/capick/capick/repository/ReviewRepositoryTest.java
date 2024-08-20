package com.capick.capick.repository;

import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

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

}