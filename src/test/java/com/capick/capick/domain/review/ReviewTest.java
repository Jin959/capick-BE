package com.capick.capick.domain.review;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    @Test
    @DisplayName("성공: 리뷰의 타입 지수를 수정할 수 있다.")
    void updateIndexes() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", "normal");

        // when
        review.updateIndexes(3, 3, 4, 3);

        // then
        assertThat(review)
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex")
                .containsExactly(3, 3, 4, 3);
    }

    @Test
    @DisplayName("예외: 타입 지수는 1 부터 5 까지만 가능하다. 1 미만 5 초과의 타입 지수를 입력받으면 예외가 발생한다.")
    void updateIndexesWithCafeTypeIndexOutOfRange() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", "normal");
        
        // when // then
        assertThatThrownBy(() -> review.updateIndexes(0, 3, 3, 3))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");
        assertThatThrownBy(() -> review.updateIndexes(6, 3, 3, 3))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");
    }

    @Test
    @DisplayName("경계: 타입 지수는 1 부터 5 까지만 가능하다. 1 과 5 는 가능해야 한다.")
    void updateIndexesWithCafeTypeIndexOutOfRangeBoundary() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", "normal");

        // when
        review.updateIndexes(1, 5, 3, 3);

        // then
        assertThat(review)
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex")
                .containsExactly(1, 5, 3, 3);
    }

    private static Review createReview(String visitPurpose, String content, String menu, String theme) {
        return Review.builder()
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .theme(theme)
                .build();
    }

}