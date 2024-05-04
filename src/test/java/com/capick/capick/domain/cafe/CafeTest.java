package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CafeTest {

    @Test
    @DisplayName("성공: 까페 타입 갱신 시 리뷰에서 매겨진 지수들이 누적되어 더해진다.")
    void updateIndexes() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3);
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo())
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex")
                .contains(3, 3, 4, 3);

    }

    @Test
    @DisplayName("성공: 까페 타입 갱신 시 누적된 지수들 중 가장 큰 값으로 까페의 타입이 정해진다.")
    void updateCafeType() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3);
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType()).isEqualByComparingTo(CafeType.COST_EFFECTIVE);

    }

    @Test
    @DisplayName("경계: 까페 타입 갱신 시 누적된 지수 중 최대값이 없으면 NONE 으로 기록한다.")
    void updateCafeTypeNone() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3);
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType()).isEqualByComparingTo(CafeType.NONE);

    }

    @Test
    @DisplayName("경계: 까페 타입 갱신 시 누적된 타입 지수가 자료형 크기를 초과할 경우에도 까페 타입 지정에는 영향이 없어야 한다.")
    void updateCafeTypeOverflow() {
        // given
        Review reviewWithMaxIntegerIndex = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3);

        // when
        cafe.updateCafeType(reviewWithMaxIntegerIndex);
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo())
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex", "cafeType")
                .containsExactly(3, 3, 4, 3, CafeType.COST_EFFECTIVE);
    }

    private static Review createReview(String visitPurpose, String content, String menu,
                                       int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex) {
        return Review.builder()
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
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