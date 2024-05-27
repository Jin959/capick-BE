package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.LocationCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.DomainLogicalException;
import com.capick.capick.exception.DomainPoliticalArgumentException;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.capick.capick.domain.cafe.CafeTheme.NORMAL;
import static com.capick.capick.domain.cafe.CafeType.COST_EFFECTIVE;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CafeRepository cafeRepository;

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
    @DisplayName("성공: 회원은 까페 후기를 남기고 공유하기 위해 등록된 까페에 대해 리뷰를 생성할 수 있다.")
    void createReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequest, registeredAt);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("writer.id", "writer.nickname", "visitPurpose", "content", "menu", "registeredAt")
                .contains(writerId, "nickname01", "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", registeredAt);
    }

    @Test
    @DisplayName("성공: 어떤 까페에 리뷰가 처음 작성되는 경우 까페가 등록 된 적이 없으므로 까페 위치정보가 함께 주어지며 까페가 등록된다.")
    void createFirstReviewWithCreateCafe() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        LocationCreateRequest locationCreateRequest
                = createLocationCreateRequest(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url", locationCreateRequest);
        ReviewCreateRequest reviewCreateRequestForCostEffectiveTypeCafe
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        reviewService.createReview(reviewCreateRequestForCostEffectiveTypeCafe, registeredAt);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1).extracting("name", "kakaoPlaceId", "kakaoDetailPageUrl")
                .contains(
                        tuple("스타벅스 광화문점", "1234567", "https://place.url")
                );

    }

    @Test
    @DisplayName("성공: 리뷰가 작성 될때 까페 타입과 테마가 갱신 된다.")
    void createReviewWithUpdateCafeTypeAndCafeTheme() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequestForCostEffectiveTypeCafe
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        reviewService.createReview(reviewCreateRequestForCostEffectiveTypeCafe, registeredAt);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("cafeTypeInfo.cafeType", "cafeThemeInfo.cafeTheme")
                .contains(
                        tuple(COST_EFFECTIVE, NORMAL)
                );

    }

    @Test
    @DisplayName("예외: 리뷰를 생성하는 회원이 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void createReviewNotExistMember() {
        // given
        Long notExistWriterId = 1L;

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest
                = createReviewCreateRequest(notExistWriterId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();
        
        // when // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequest, registeredAt))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");

    }

    @Test
    @DisplayName("예외: 등록된 까페에만 리뷰를 작성할 수 있다. 첫 리뷰 생성 시 까페가 등록된 적이 없어 등록하려는데 까페의 위치 정보를 받지 않으면 예외가 발생한다.")
    void createFirstReviewWithoutLocation() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequest, registeredAt))
                .isInstanceOf(DomainLogicalException.class)
                .hasMessage("까페에 첫 리뷰를 등록할 때는 까페의 위치 정보가 필요합니다.");

    }

    @Test
    @DisplayName("예외: 리뷰 작성 시 간접적인 설문을 통해 까페의 타입 지수를 1 에서 5 로 매길 수 있다. 1 부터 5 이외의 타입 지수를 입력받으면 예외가 발생한다.")
    void createReviewWithCafeTypeIndexOutOfRange() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequestMinus
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 0, 3, 3, 3, "normal");
        ReviewCreateRequest reviewCreateRequestOver
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 6, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequestMinus, registeredAt))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequestOver, registeredAt))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");

    }

    @Test
    @DisplayName("예외: 리뷰 작성 시 사진을 3개 보다 많이 등록 할 경우 도메인 정책상 예외가 발생한다.")
    void createReviewWithImagesExceeded() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url", "https://image4.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",3, 3, 3, 3, "normal", imageUrls);

        LocalDateTime registeredAt = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(reviewCreateRequest, registeredAt))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("이미지는 최대 3개 까지 등록할 수 있습니다.");
    }

    @Test
    @DisplayName("경계: 리뷰 작성 시 간접적인 설문을 통해 까페의 타입 지수를 1 에서 5 로 매길 수 있다. 1 과 5 는 가능해야 한다.")
    void createReviewWithCafeTypeIndexOutOfRangeBoundary() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest
                = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest
                = createReviewCreateRequest(writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 1, 3, 5, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        reviewService.createReview(reviewCreateRequest, registeredAt);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("cafeTypeInfo.cafeType")
                .contains(COST_EFFECTIVE);

    }

    @Test
    @DisplayName("경계: 리뷰 작성 시 사진을 3개까지는 등록 할 수 있다.")
    void createReviewWithMaxNumberOfImages() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest("스타벅스 광화문점", "1234567", "https://place.url");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",3, 3, 3, 3, "normal", imageUrls);

        LocalDateTime registeredAt = LocalDateTime.now();

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequest, registeredAt);

        // then
        assertThat(response.getImageUrls()).hasSize(3)
                .containsOnly("https://image1.url", "https://image2.url", "https://image3.url");
    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
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

    private LocationCreateRequest createLocationCreateRequest(double latitude, double longitude, String address, String roadAddress) {
        return LocationCreateRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .roadAddress(roadAddress)
                .build();
    }

    private CafeCreateRequest createCafeCreateRequest(
            String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return CafeCreateRequest.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

    private CafeCreateRequest createCafeCreateRequest(
            String name, String kakaoPlaceId, String kakaoDetailPageUrl, LocationCreateRequest location) {
        return CafeCreateRequest.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .location(location)
                .build();
    }

    private ReviewCreateRequest createReviewCreateRequest(
            Long writerId, CafeCreateRequest cafe, String visitPurpose, String content,
            String menu, int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme) {
        return ReviewCreateRequest.builder()
                .writerId(writerId)
                .cafe(cafe)
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

    private ReviewCreateRequest createReviewCreateRequest(
            Long writerId, CafeCreateRequest cafe, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, List<String> imageUrls) {
        return ReviewCreateRequest.builder()
                .writerId(writerId)
                .cafe(cafe)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .spaceIndex(spaceIndex)
                .priceIndex(priceIndex)
                .noiseIndex(noiseIndex)
                .theme(theme)
                .imageUrls(imageUrls)
                .build();
    }

}