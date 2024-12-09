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
import java.util.stream.Stream;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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

    @Test
    @DisplayName("성공: 리뷰들의 리뷰 이미지를 썸네일용으로 하나씩 조회한다. ID가 가장 낮은 이미지로 조회하고 리뷰별로 한 개 이상 조회되지 않는다.")
    void findReviewThumbnailsBy() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        List<Review> reviews = List.of(
                createReview("넓어서 갔어요", "리뷰 내용1", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt),
                createReview("넓어서 갔어요", "리뷰 내용2", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt),
                createReview("넓어서 갔어요", "리뷰 내용3", "핫 아메리카노", 3, 3, 4, 3, "normal", registeredAt)
        );
        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        reviewImageRepository.saveAll(Stream.of(
                                "https://storage.com/images/12345",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ).map(imageUrl -> createReviewImage(imageUrl, reviews.get(0))).collect(Collectors.toList())
        );
        reviewImageRepository.save(createReviewImage("https://storage.com/images/80459", reviews.get(1)));

        // when
        List<ReviewImage> reviewImages = reviewImageRepository.findReviewThumbnailsBy(savedReviews, ACTIVE);

        // then
        assertThat(reviewImages).hasSize(2)
                .extracting("review.id", "imageUrl")
                .containsExactlyInAnyOrder(
                        tuple(savedReviews.get(0).getId(), "https://storage.com/images/12345"),
                        tuple(savedReviews.get(1).getId(), "https://storage.com/images/80459")
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