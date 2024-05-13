package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CafeTest {

    @Test
    @DisplayName("성공: 까페 타입 갱신 시 리뷰에서 매겨진 지수들이 누적되어 더해진다.")
    void updateIndexes() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo())
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex")
                .containsExactly(3, 3, 4, 3);

    }

    @Test
    @DisplayName("성공: 까페 타입 갱신 시 누적된 지수들 중 가장 큰 값으로 까페의 타입이 정해진다.")
    void updateCafeType() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType()).isEqualByComparingTo(CafeType.COST_EFFECTIVE);

    }

    @Test
    @DisplayName("경계: 까페 타입 갱신 시 누적된 지수 중 최대값이 없으면 갱신 이전 까페 타입을 유지한다.")
    void updateCafeTypeWithoutMaxIndex() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal");
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
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        // when
        cafe.updateCafeType(reviewWithMaxIntegerIndex);
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo())
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex", "cafeType")
                .containsExactly(3, 3, 4, 3, CafeType.COST_EFFECTIVE);
    }

    @Test
    @DisplayName("성공: 까페 테마 갱신 시 리뷰에서 전달 받은 까페 테마 횟수를 세어서 테마 누적 횟수로 기록한다.")
    void updateThemeCount() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "vibe");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeTheme(review);

        // then
        assertThat(cafe.getCafeThemeInfo())
                .extracting("normalCount", "vibeCount", "viewCount", "petCount",
                        "hobbyCount", "studyCount", "kidsCount", "etcCount")
                .containsExactly(0, 1, 0, 0, 0, 0, 0, 0);
    }

    @Test
    @DisplayName("성공: 까페 테마 갱신 시 테마 누적 횟수가 가장 큰 테마로 정해진다.")
    void updateCafeTheme() {
        // given
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "vibe");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        cafe.updateCafeTheme(review);

        // then
        assertThat(cafe.getCafeThemeInfo().getCafeTheme()).isEqualByComparingTo(CafeTheme.VIBE);
    }

    @Test
    @DisplayName("경계: 까페 테마 갱신 시 테마 누적 횟수 중 최대값이 없으면 갱신 이전 테마를 유지한다.")
    void updateCafeThemeWithoutMaxThemeCount() {
        // given
        Review review1 = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "vibe");
        Review review2 = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
        Review review3 = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "view");
        List<Review> reviews = List.of(review1, review2, review3);

        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // when
        reviews.forEach(cafe::updateCafeTheme);

        // then
        assertThat(cafe.getCafeThemeInfo().getCafeTheme()).isEqualByComparingTo(CafeTheme.VIBE);
    }

    // TODO: 테마 Overflow 테스트 방법 고민해보기, 테스트 수행도 비용이다. 21억번 까페 테마 누적을 시키는 것은 너무 고비용이다. 테스트 돌리는데 오래 걸림.
//    @Test
//    @DisplayName("경계: 까페 테마 갱신 시 테마 누적 횟수가 자료형 크기를 초과할 경우에도 까페 테마 지정에는 영향이 없어야 한다.")
//    void updateCafeThemeOverflow() {
//        // given
//        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "vibe");
//        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
//        Review newReview = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
//
//        // when
//        for (int i = 0; i < Integer.MAX_VALUE; i++) {
//            cafe.updateCafeType(review);
//        }
//        cafe.updateCafeType(newReview);
//
//        // then
//        assertThat(cafe.getCafeThemeInfo())
//                .extracting("vibeCount", "normalCount", "cafeTheme")
//                .containsExactly(Integer.MAX_VALUE / 2, 1, CafeTheme.VIBE);
//    }

    private static Review createReview(String visitPurpose, String content, String menu,
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

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

}