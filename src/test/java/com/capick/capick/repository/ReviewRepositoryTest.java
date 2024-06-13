package com.capick.capick.repository;

import com.capick.capick.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("성공: 삭제되지 않았거나 작성된 리뷰를 조회할 수 있다.")
    void findByIdAndStatus() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal", registeredAt);
        reviewRepository.save(review);

        // when
        Optional<Review> optionalReview = reviewRepository.findByIdAndStatus(review.getId(), ACTIVE);

        // then
        assertThat(optionalReview.isPresent()).isTrue();
        assertThat(optionalReview.get()).usingRecursiveComparison().isEqualTo(review);
    }

    private Review createReview(String visitPurpose, String content, String menu, int coffeeIndex, int spaceIndex,
                                int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
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

}