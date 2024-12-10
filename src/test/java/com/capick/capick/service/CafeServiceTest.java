package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.PageResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.MemberRepository;
import com.capick.capick.repository.ReviewImageRepository;
import com.capick.capick.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.capick.capick.domain.cafe.CafeTheme.NORMAL;
import static com.capick.capick.domain.cafe.CafeType.*;
import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
class CafeServiceTest {

    @Autowired
    private CafeService cafeService;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @AfterEach
    void tearDown() {
        reviewImageRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        cafeRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 회원 또는 방문자는 카카오 지도와 같은 외부 지도 서비스 업체에서 제공한 ID로 카페 정보를 조회할 수 있다.")
    void getCafeByMapVendorPlaceId() {
        // given
        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url/1234567", cafeLocation);
        String kakaoPlaceId = cafeRepository.save(cafe).getKakaoPlaceId();

        // when
        CafeResponse response = cafeService.getCafeByMapVendorPlaceId(kakaoPlaceId);

        // then
        assertThat(response)
                .extracting("name", "kakaoPlaceId", "kakaoDetailPageUrl", "cafeType", "cafeTheme")
                .contains("스타벅스 광화문점", "1234567", "https://place.url/1234567", NONE, NORMAL);
        assertThat(response.getLocation())
                .extracting("latitude", "longitude", "address", "roadAddress")
                .contains(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
    }

    @Test
    @DisplayName("예외: 지도 서비스상의 ID로 카페 조회 시 등록된 적이 없거나 삭제되어 존재하지 않는 카페이면 예외가 발생한다.")
    void getNotExistCafeByMapVendorPlaceId() {
        // given
        String notCreatedCafeKakaoPlaceId = "1234567";

        // when // then
        assertThatThrownBy(() -> cafeService.getCafeByMapVendorPlaceId(notCreatedCafeKakaoPlaceId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("등록된 적이 없거나 삭제되어 서비스상에서 존재하지 않는 카페입니다.");
    }

    @Test
    @DisplayName("성공: 회원 또는 방문자는 특정 카페 후기를 보기 위해 카페별 리뷰를 모아서 페이지별로 확인 할 수 있다. 썸네일은 가장 먼저 등록된 이미지로 가져온다.")
    void getReviewsByCafeWithMapVendorPlaceId() {
        // given
        List<Member> writers = List.of(
                createMember("email01@naver.com", "password01%^&", "nickname01"),
                createMember("email02@naver.com", "password01%^&", "nickname02"),
                createMember("email03@naver.com", "password01%^&", "nickname03")
        );
        memberRepository.saveAll(writers);

        Cafe targetCafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url/1234567",
                createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000"));
        Cafe savedTargetCafe = cafeRepository.save(targetCafe);

        Cafe dummyCafe = createCafe("폴바셋 광화문점", "7654321", "https://place.url/7654321",
                createLocation(37.574912399424, 126.9789650731, "서울 종로구 중학동 00-0", "서울 종로구 종로1길 000"));
        cafeRepository.save(dummyCafe);

        LocalDateTime registeredAtFirst = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        List<LocalDateTime> registerTimes = IntStream.range(0, 5)
                .mapToObj(registeredAtFirst::plusMinutes).collect(Collectors.toList());

        List<Review> reviews = List.of(
                createReview(
                        writers.get(0), targetCafe, "넓어서 갔어요", "리뷰 내용1", "핫 아메리카노", 3, 4, 3, 3, "normal",
                        registerTimes.get(0)),
                createReview(
                        writers.get(1), targetCafe, "넓어서 갔어요", "리뷰 내용2", "핫 아메리카노", 3, 4, 3, 3, "normal",
                        registerTimes.get(1)),
                createReview(
                        writers.get(1), targetCafe, "넓어서 갔어요", "리뷰 내용3", "핫 아메리카노", 3, 4, 3, 3, "normal",
                        registerTimes.get(2)),
                createReview(
                        writers.get(1), targetCafe, "넓어서 갔어요", "리뷰 내용4", "핫 아메리카노", 3, 4, 3, 3, "normal",
                        registerTimes.get(3)),
                createReview(
                        writers.get(2), dummyCafe, "일하거나 책읽기 좋아요", "리뷰 내용5", "핫 아메리카노", 4, 3, 3, 3, "normal",
                        registerTimes.get(4))
        );
        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        reviewImageRepository.saveAll(List.of(
                                "https://storage.com/images/12345",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ).stream()
                        .map(imageUrl -> createReviewImage(imageUrl, reviews.get(3))).collect(Collectors.toList())
        );
        reviewImageRepository.save(createReviewImage("https://storage.com/images/80459", reviews.get(2)));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "registeredAt"));

        // when
        PageResponse<ReviewSimpleResponse> response = cafeService
                .getReviewsByCafeWithMapVendorPlaceId(savedTargetCafe.getKakaoPlaceId(), pageRequest);

        // then
        List<ReviewSimpleResponse> content = response.getContent();
        assertThat(response)
                .extracting("pageNumber", "pageSize", "totalPages", "totalElements", "hasNext", "hasPrevious")
                .contains(0, 3, 2, 4L, true, false);
        assertThat(content).hasSize(3)
                .extracting("id", "visitPurpose", "content", "menu", "registeredAt", "thumbnailUrl")
                .containsExactly(
                        tuple(savedReviews.get(3).getId(), "넓어서 갔어요", "리뷰 내용4", "핫 아메리카노",
                                registerTimes.get(3), "https://storage.com/images/12345"),
                        tuple(savedReviews.get(2).getId(), "넓어서 갔어요", "리뷰 내용3", "핫 아메리카노",
                                registerTimes.get(2), "https://storage.com/images/80459"),
                        tuple(savedReviews.get(1).getId(), "넓어서 갔어요", "리뷰 내용2", "핫 아메리카노",
                                registerTimes.get(1), null)
                );
        assertThat(content)
                .extracting(ReviewSimpleResponse::getCafe)
                .extracting("name", "kakaoPlaceId")
                .contains(
                        tuple("스타벅스 광화문점", "1234567")
                )
                .doesNotContain(
                        tuple("폴바셋 광화문점", "7654321")
                );
    }

    @Test
    @DisplayName("예외: 카페별 리뷰 페이지네이션 조회 시 등록된 적이 없거나 삭제되어 존재하지 않는 카페이면 예외가 발생한다.")
    void getReviewsByNotExistCafeWithMapVendorPlaceId() {
        // given
        String notCreatedCafeKakaoPlaceId = "1234567";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "registeredAt"));

        // when // then
        assertThatThrownBy(() -> cafeService.getReviewsByCafeWithMapVendorPlaceId(notCreatedCafeKakaoPlaceId, pageRequest))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("등록된 적이 없거나 삭제되어 서비스상에서 존재하지 않는 카페입니다.");
    }

    private Location createLocation(double latitude, double longitude, String address, String roadAddress) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .roadAddress(roadAddress)
                .build();
    }

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl, Location location) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .location(location)
                .build();
    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private Review createReview(
            Member writer, Cafe cafe, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
        return Review.builder()
                .writer(writer)
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

    private ReviewImage createReviewImage(String imageUrl, Review review) {
        return ReviewImage.builder()
                .imageUrl(imageUrl)
                .review(review)
                .build();
    }

}