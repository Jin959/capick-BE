package com.capick.capick.repository;

import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class ReviewImageRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Test
    @DisplayName("성공: 리뷰에 업로드 된 리뷰 이미지를 해당 리뷰로 조회할 수 있다.")
    void findAllByReviewAndStatus() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Review review = createReview("넓어서 갔어요", "리뷰 내용", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt);
        Review savedReview = reviewRepository.save(review);

        List<String> imageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        reviewImageRepository.saveAll(
                imageUrls.stream()
                        .map(imageUrl -> createReviewImage(imageUrl, review)).collect(Collectors.toList())
        );

        // when
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewAndStatus(savedReview, ACTIVE);

        // then
        assertThat(reviewImages).hasSize(3)
                .extracting(ReviewImage::getImageUrl)
                .containsExactlyInAnyOrder(
                        "https://storage.com/images/80459",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                );
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

    private ReviewImage createReviewImage(String imageUrl, Review review) {
        return ReviewImage.builder()
                .imageUrl(imageUrl)
                .review(review)
                .build();
    }

}