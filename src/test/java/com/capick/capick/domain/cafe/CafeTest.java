package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import com.capick.capick.dto.request.LocationCreateRequest;
import com.capick.capick.exception.DomainLogicalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CafeTest {

    @Test
    @DisplayName("성공: 카페 생성 시 타입 초기 값은 NONE 이다.")
    void createCafeInitType() {
        // given
        LocationCreateRequest locationCreateRequest
                = createLocationCreateRequest(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");

        // when
        Cafe cafe = Cafe.create("스타벅스 광화문점", "1234567", "https://place.url", locationCreateRequest);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType()).isEqualByComparingTo(CafeType.NONE);

    }

    @Test
    @DisplayName("성공: 카페 생성 시 테마 초기 값은 NORMAL 이다.")
    void createCafeInitTheme() {
        // given
        LocationCreateRequest locationCreateRequest
                = createLocationCreateRequest(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");

        // when
        Cafe cafe = Cafe.create("스타벅스 광화문점", "1234567", "https://place.url", locationCreateRequest);

        // then
        assertThat(cafe.getCafeThemeInfo().getCafeTheme()).isEqualByComparingTo(CafeTheme.NORMAL);

    }

    @Test
    @DisplayName("예외: 카페 생성 시 위치 정보가 없으면 예외가 발생한다.")
    void createCafeWithoutLocation() {
        // given
        LocationCreateRequest locationCreateRequest = null;

        // when // then
        assertThatThrownBy(() -> Cafe.create("스타벅스 광화문점", "1234567", "https://place.url", locationCreateRequest))
                .isInstanceOf(DomainLogicalException.class)
                .hasMessage("까페에 첫 리뷰를 등록할 때는 까페의 위치 정보가 필요합니다.");

    }

    @Test
    @DisplayName("성공: 카페 타입 갱신 시 리뷰에서 매겨진 지수들이 누적되어 더해진다.")
    void addCafeTypeIndexes() {
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
    @DisplayName("성공: 카페 타입 갱신 시 누적된 지수들 중 가장 큰 값으로 카페의 타입이 정해진다.")
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
    @DisplayName("경계: 카페 타입 갱신 시 누적된 지수 중 최대값이 없으면 갱신 이전 카페 타입을 유지한다.")
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
    @DisplayName("경계: 카페 타입 갱신 시 누적된 타입 지수가 자료형 크기를 초과할 경우에도 카페 타입 지정에는 영향이 없어야 한다.")
    void updateCafeTypeOverflow() {
        // given
        Review reviewWithMaxIntegerIndex = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",
                Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 1, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3 + 2, 3, "normal");

        // when
        // TODO: 두 행위 각각에 대해 카페 타입이 변했는지 시나리오 테스트 DynamicTest 를 사용해야 할 것 같다. 확실하지는 않음.
        cafe.updateCafeType(reviewWithMaxIntegerIndex);
        cafe.updateCafeType(review);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType()).isEqualByComparingTo(CafeType.COST_EFFECTIVE);
    }

    @Test
    @DisplayName("성공: 카페 테마 갱신 시 리뷰에서 선택된 카페 테마는 테마 누적 횟수에 하나씩 누적되어 더해진다.")
    void addCafeThemeCount() {
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
    @DisplayName("성공: 카페 테마 갱신 시 테마 누적 횟수가 가장 큰 테마로 정해진다.")
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
    @DisplayName("경계: 카페 테마 갱신 시 테마 누적 횟수 중 최대값이 없으면 갱신 이전 테마를 유지한다.")
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

    // TODO: 테마 Overflow 테스트 방법 고민해보기, 테스트 수행도 비용이다. 21억번 카페 테마 누적을 시키는 것은 너무 고비용이다. 테스트 돌리는데 오래 걸림.
//    @Test
//    @DisplayName("경계: 카페 테마 갱신 시 테마 누적 횟수가 자료형 크기를 초과할 경우에도 카페 테마 지정에는 영향이 없어야 한다.")
//    void updateCafeThemeOverflow() {
//        // given
//        Review review = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "vibe");
//        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
//        Review newReview = createReview("일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");
//
//        // when
//        for (int i = 0; i < Integer.MAX_VALUE; i++) {
//            cafe.updateCafeTheme(review);
//        }
//        cafe.updateCafeTheme(newReview);
//
//        // then
//        assertThat(cafe.getCafeThemeInfo())
//                .extracting("vibeCount", "normalCount", "cafeTheme")
//                .containsExactly(Integer.MAX_VALUE / 2, 1, CafeTheme.VIBE);
//    }

    @Test
    @DisplayName("성공: 카페 타입 감소 갱신 시 리뷰에서 매겼던 카페 타입 지수만큼 누적된 지수를 감소시켜 되돌린다.")
    void deductCafeTypeIndexes() {
        // given
        Review review = createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 3, 4, 3, 3, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다. JPA 임베디드 타입의 공유 참조 문제를 방지 하기위해 Cafe의 빌더로 테스트 환경을 설정할 수 없다. 좋은 방법이 없는지 찾아보기
        cafe.updateCafeType(review);

        // when
        cafe.updateCafeTypeByDeducting(review);

        // then
        assertThat(cafe.getCafeTypeInfo())
                .extracting("coffeeIndex", "spaceIndex", "priceIndex", "noiseIndex")
                .containsExactly(0, 0, 0, 0);
    }

    @Test
    @DisplayName("성공: 카페 타입 지수를 감소시켜 카페 타입을 갱신할 때 누적된 지수들 중 가장 큰 값으로 카페의 타입이 정해진다.")
    void updateCafeTypeByDeducting() {
        // given
        Review reviewTypeSpacious = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 3, 4, 3, 3, "normal");
        Review reviewTypeCoffee = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 5, 3, 3, 3, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다.
        cafe.updateCafeType(reviewTypeSpacious);
        cafe.updateCafeType(reviewTypeCoffee);

        // when
        cafe.updateCafeTypeByDeducting(reviewTypeCoffee);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType())
                .isEqualByComparingTo(CafeType.SPACIOUS)
                .isNotEqualByComparingTo(CafeType.COFFEE);
    }

    @Test
    @DisplayName("예외: 카페 타입 지수를 차감할 때 누적 타입 지수보다 많이 차감시킬 수 없다. 그렇지 않으면 예외를 발생시킨다. 누적된 카페 타입 지수는 양수이다.")
    void updateCafeTypeByDeductingMoreThanAccumulated() {
        // given
        Review review = createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 1, 1, 1, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);

        Review reviewWithCafeTypeIndexesMoreThanAccumulated = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 5, 5, 5, 5, "normal");

        // when // then
        assertThatThrownBy(() -> cafe.updateCafeTypeByDeducting(reviewWithCafeTypeIndexesMoreThanAccumulated))
                .isInstanceOf(DomainLogicalException.class)
                .hasMessage("차감할 누적 카페 타입 지수가 없습니다. 이전에 등록한 만큼 차감해주세요.");
    }

    @Test
    @DisplayName("경계: 카페 타입 감소 갱신 후 누적된 지수 중 최대값이 없으면 감소 갱신 이전의 카페 타입을 유지한다.")
    void updateCafeTypeByDeductingWithoutMaxIndex() {
        // given
        Review reviewTypeNone = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 2, 2, 2, 2, "normal");
        Review reviewTypeSpacious = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 2, 4, 2, 2, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다.
        cafe.updateCafeType(reviewTypeNone);
        cafe.updateCafeType(reviewTypeSpacious);

        // when
        cafe.updateCafeTypeByDeducting(reviewTypeSpacious);

        // then
        assertThat(cafe.getCafeTypeInfo().getCafeType())
                .isEqualByComparingTo(CafeType.SPACIOUS)
                .isNotEqualByComparingTo(CafeType.NONE);
    }

    @Test
    @DisplayName("성공: 카페 테마 감소 갱신 시 리뷰에서 선택했던 테마의 누적 횟수를 감소시켜 되돌린다.")
    void deductCafeThemeCount() {
        // given
        Review review = createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 3, 4, 3, 3, "study");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다. JPA 임베디드 타입의 공유 참조 문제를 방지 하기위해 Cafe의 빌더로 테스트 환경을 설정할 수 없다. 좋은 방법이 없는지 찾아보기
        cafe.updateCafeTheme(review);

        // when
        cafe.updateCafeThemeByDeducting(review);

        // then
        assertThat(cafe.getCafeThemeInfo())
                .extracting(
                        "normalCount", "vibeCount", "viewCount", "petCount",
                        "hobbyCount", "studyCount", "kidsCount", "etcCount"
                )
                .containsExactly(
                        0, 0, 0, 0,
                        0, 0, 0, 0
                );
    }

    @Test
    @DisplayName("성공: 카페 테마 누적 횟수를 감소시켜 카페 테마를 갱신할 때 누적된 테마 횟수들 중 가장 큰 값으로 카페의 테마가 정해진다.")
    void updateCafeThemeByDeducting() {
        // given
        Review reviewThemeStudy = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 5, 3, 3, 3, "study");
        List<Review> reviewsThemeNormal = List.of(
                createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 3, 4, 3, 3, "normal"),
                createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 5, 3, 3, 3, "normal")
        );
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");

        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다.
        cafe.updateCafeTheme(reviewThemeStudy);
        reviewsThemeNormal.forEach(cafe::updateCafeTheme);

        // when
        reviewsThemeNormal.forEach(cafe::updateCafeThemeByDeducting);

        // then
        assertThat(cafe.getCafeThemeInfo().getCafeTheme())
                .isEqualByComparingTo(CafeTheme.STUDY)
                .isNotEqualByComparingTo(CafeTheme.NORMAL);
    }

    @Test
    @DisplayName("예외: 카페 테마 횟수를 차감할 때 누적 테마 횟수보다 많이 차감시킬 수 없다. 그렇지 않으면 예외를 발생시킨다. 누적된 카페 테마 횟수는 양수이다.")
    void updateCafeThemeByDeductingMoreThanAccumulated() {
        // given
        Review review = createReview("일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 1, 1, 1, "normal");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeTheme(review);

        Review reviewWithCafeThemeUnselected = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 1, 1, 1, "study");

        // when // then
        assertThatThrownBy(() -> cafe.updateCafeThemeByDeducting(reviewWithCafeThemeUnselected))
                .isInstanceOf(DomainLogicalException.class)
                .hasMessage("차감할 카페 테마 횟수가 없습니다. 이전에 등록한 테마를 입력해주세요.");
    }

    @Test
    @DisplayName("경계: 카페 테마 감소 갱신 후 테마 누적 횟수 중 최대값이 없으면 감소 갱신 이전의 카페 테마를 유지한다.")
    void updateCafeThemeByDeductingWithoutMaxThemeCount() {
        // given
        Review reviewThemeStudy = createReview(
                "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 5, 3, 3, 3, "study");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        // TODO: 같은 객체이나 다른 행위를 끌어다 사용했다.
        cafe.updateCafeTheme(reviewThemeStudy);

        // when
        cafe.updateCafeThemeByDeducting(reviewThemeStudy);

        // then
        assertThat(cafe.getCafeThemeInfo().getCafeTheme())
                .isEqualByComparingTo(CafeTheme.STUDY)
                .isNotEqualByComparingTo(CafeTheme.ETC);
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

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

    private LocationCreateRequest createLocationCreateRequest(double latitude, double longitude, String address, String roadAddress) {
        return LocationCreateRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .roadAddress(roadAddress)
                .build();
    }

}