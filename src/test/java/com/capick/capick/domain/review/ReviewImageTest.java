package com.capick.capick.domain.review;

import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewImageTest {

    @Test
    @DisplayName("성공: 리뷰 이미지 생성 시 중복 된 이미지가 있을 경우 중복은 제거된다.")
    void createReviewImagesWithDuplicateImages() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image2.url");

        // when
        List<ReviewImage> reviewImages = ReviewImage.createReviewImages(imageUrls, review);

        // then
        assertThat(reviewImages).hasSize(2)
                .extracting(ReviewImage::getImageUrl)
                .containsExactlyInAnyOrder("https://image1.url", "https://image2.url");
    }

    @Test
    @DisplayName("예외: 리뷰 이미지 생성 시 최대 3개 까지만 가능하다. 넘어가면 예외가 발생한다.")
    void createReviewImagesExceeded() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url", "https://image4.url");

        // when // then
        assertThatThrownBy(() -> ReviewImage.createReviewImages(imageUrls, review))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("이미지는 최대 3개 까지 등록할 수 있습니다.");
    }

    @Test
    @DisplayName("경계: 리뷰 이미지 생성 시 3개까지는 등록 할 수 있다.")
    void createReviewImagesMaxNumberOfImages() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url");

        // when
        List<ReviewImage> reviewImages = ReviewImage.createReviewImages(imageUrls, review);

        // then
        assertThat(reviewImages).hasSize(3)
                .extracting(ReviewImage::getImageUrl)
                .containsExactlyInAnyOrder("https://image1.url", "https://image2.url", "https://image3.url");
    }

    @Test
    @DisplayName("성공: 리뷰 이미지 삭제 시 소프트 딜리트 된다.")
    void delete() {
        // given
        Review review = createReview("넓어서 갔어요", "리뷰 내용", "핫 아메리카노", 2, 2, 4, 2, "normal");
        String imageUrl = "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2";
        ReviewImage reviewImage = createReviewImage(imageUrl, review);

        // when
        reviewImage.delete();

        // then
        assertThat(reviewImage.getStatus()).isEqualByComparingTo(BaseStatus.INACTIVE);
    }

    private Review createReview(String visitPurpose, String content, String menu,
                                int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme) {
        return Review.builder()
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .theme(theme)
                .build();
    }

    private ReviewImage createReviewImage(String imageUrl, Review review) {
        return ReviewImage.builder()
                .imageUrl(imageUrl)
                .review(review)
                .build();
    }

}