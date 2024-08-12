package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.LocationCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.request.ReviewUpdateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.DomainLogicalException;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.exception.UnauthorizedException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.domain.cafe.CafeTheme.*;
import static com.capick.capick.domain.cafe.CafeType.*;
import static com.capick.capick.domain.common.BaseStatus.*;
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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        LocationCreateRequest locationCreateRequest = createLocationCreateRequest(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url", locationCreateRequest);
        ReviewCreateRequest reviewCreateRequestForCostEffectiveTypeCafe = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequestForCostEffectiveTypeCafe = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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
    @DisplayName("성공: 리뷰 작성 시 중복 된 사진이 업로드 될 경우 중복은 제거하고 저장한다.")
    void createReviewWithDuplicateImages() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Long writerId = writer.getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image2.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal", imageUrls);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequest, registeredAt);

        // then
        assertThat(response.getImageUrls()).hasSize(2)
                .containsExactlyInAnyOrder("https://image1.url", "https://image2.url");
    }

    @Test
    @DisplayName("예외: 리뷰를 생성하는 회원이 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void createReviewNotExistMember() {
        // given
        Long notExistWriterId = 1L;

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                notExistWriterId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        
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

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequestMinus = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 0, 3, 3, 3, "normal");
        ReviewCreateRequest reviewCreateRequestOver = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 6, 3, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        List<String> imageUrls = List.of(
                "https://image1.url", "https://image2.url", "https://image3.url", "https://image4.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",3, 3, 3, 3, "normal", imageUrls);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 1, 3, 5, 3, "normal");

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        CafeCreateRequest cafeCreateRequest = createCafeCreateRequest(
                "스타벅스 광화문점", "1234567", "https://place.url");
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url");
        ReviewCreateRequest reviewCreateRequest = createReviewCreateRequest(
                writerId, cafeCreateRequest, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노",3, 3, 3, 3, "normal", imageUrls);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        // when
        ReviewResponse response = reviewService.createReview(reviewCreateRequest, registeredAt);

        // then
        assertThat(response.getImageUrls()).hasSize(3)
                .containsExactlyInAnyOrder("https://image1.url", "https://image2.url", "https://image3.url");
    }

    @Test
    @DisplayName("성공: 방문자 및 회원은 까페 정보와 후기를 보기 위해 등록된 리뷰를 조회할 수 있다.")
    void getReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", 3, 3, 4, 3, "normal", registeredAt);

        Review reviewWithImages = createReview(
                writer, cafe, "일하거나 책읽기 좋아요", "리뷰 내용", "라떼", 3, 3, 4, 3, "vibe", registeredAt);
        List<String> imageUrls = List.of("https://image1.url", "https://image2.url", "https://image3.url");
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> createReviewImage(imageUrl, reviewWithImages)).collect(Collectors.toList());

        List<Long> reviewIds = reviewRepository.saveAll(List.of(review, reviewWithImages)).stream()
                .map(Review::getId).collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImages);

        // when
        List<ReviewResponse> responses = reviewIds.stream()
                .map(reviewId -> reviewService.getReview(reviewId)).collect(Collectors.toList());

        // then
        assertThat(responses.get(0))
                .extracting("id", "writer.id", "writer.nickname",
                        "visitPurpose", "content", "menu", "registeredAt")
                .contains(review.getId(), writer.getId(), writer.getNickname(),
                        "일하거나 책읽기 좋아요", "리뷰 내용", "아메리카노", registeredAt);
        assertThat(responses.get(1))
                .extracting("id", "writer.id", "writer.nickname",
                        "visitPurpose", "content", "menu", "registeredAt", "imageUrls")
                .contains(reviewWithImages.getId(), writer.getId(), writer.getNickname(),
                        "일하거나 책읽기 좋아요", "리뷰 내용", "라떼", registeredAt, imageUrls);
    }

    @Test
    @DisplayName("예외: 리뷰 조회 시 삭제 되었거나 존재하지 않는 리뷰이면 예외가 발생한다.")
    void getNotExistReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);

        Long notCreatedReviewId = 1L;
        
        // when // then
        assertThatThrownBy(() -> reviewService.getReview(notCreatedReviewId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
    }

    @Test
    @DisplayName("성공: 회원은 자기가 작성한 리뷰를 수정할 수 있다.")
    void updateReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "넓어서 갔어요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        List<String> imageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> createReviewImage(imageUrl, review)).collect(Collectors.toList());

        Long reviewId = reviewRepository.save(review).getId();
        reviewImageRepository.saveAll(reviewImages);

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 4, 1, 1, "vibe");

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        assertThat(response)
                .extracting("writer.id", "writer.nickname", "visitPurpose", "content", "menu")
                .contains(writerId, "nickname01", "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼");
    }

    @Test
    @DisplayName("성공: 리뷰 수정 시 카페 타입 지수나 카페 테마를 수정하면 이전 기록은 없었던 것으로 하고 수정한 것을 적용해 카페 타입 및 테마를 갱신한다.")
    void updateReviewWithUpdateCafeTypeAndCafeTheme() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 1, 1, 4, "normal");

        // when
        reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("cafeTypeInfo.cafeType", "cafeThemeInfo.cafeTheme")
                .contains(
                        tuple(NOISY, NORMAL)
                );
    }

    @Test
    @DisplayName("성공: 리뷰 수정 시 이미지를 수정하면 없어진 이미지는 삭제하고 새 이미지는 추가한다.")
    void updateReviewWithReviewImages() {
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "넓어서 갔어요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "normal", registeredAt);
        List<String> imageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> createReviewImage(imageUrl, review)).collect(Collectors.toList());

        Long reviewId = reviewRepository.save(review).getId();
        reviewImageRepository.saveAll(reviewImages);

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        List<String> newImageUrls = List.of(
                "https://storage.com/images/new",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 4, 1, 1, "normal", newImageUrls);

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        assertThat(response.getImageUrls()).hasSize(2)
                .containsExactlyInAnyOrder(
                        "https://storage.com/images/new",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                );
    }

    @Test
    @DisplayName("성공: 리뷰 수정 시 중복 된 사진이 업로드 될 경우 중복은 제거하고 저장한다.")
    void updateReviewWithDuplicateImages() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        List<String> newImageUrls = List.of(
                "https://storage.com/images/new/duplicate",
                "https://storage.com/images/new/duplicate",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", newImageUrls);

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        assertThat(response.getImageUrls()).hasSize(2)
                .containsExactlyInAnyOrder(
                        "https://storage.com/images/new/duplicate",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                );
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 수정할 리뷰가 존재하지 않으면 예외가 발생한다.")
    void updateNotExistReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Long notExistReviewId = 1L;

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 4, 1, 1, "vibe");

        // when // then
        assertThatThrownBy(() -> reviewService.updateReview(notExistReviewId, reviewUpdateRequest))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
    }

    @Test
    @DisplayName("예외: 리뷰를 수정하는 회원이 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void updateReviewByNotExistMember() {
        // given
        // TODO: 테스트 환경으로 삭제된 회원을 만들기 위해 Member.delete 라는 다른 객체의 행위를 끌어다 사용했다. 한편, Member의 빌더에 status 필드를 수정할 수 있게 하려 했으나 다른 곳에서 탈퇴 된 회원을 만들 가능성을 없애기 위해 빌더에 추가하는 것을 그만두었다. 좋은 방법이 없을까?
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        writer.delete();
        Long deletedWriterId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                deletedWriterId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 4, 1, 1, "vibe");

        // when // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, reviewUpdateRequest))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("예외: 리뷰는 작성자만 수정할 수 있다. 그렇지 않은 경우 예외가 발생한다.")
    void updateReviewByNotTheWriter() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer);
        Member anotherMember = createMember("email02@naver.com", "password02%^&", "nickname02");
        Long anotherMemberId = memberRepository.save(anotherMember).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                anotherMemberId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 4, 1, 1, "vibe");

        // when // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, reviewUpdateRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("작성자가 아닙니다.");
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 카페의 타입 지수를 1 에서 5 로 매길 수 있다. 1 부터 5 이외의 타입 지수를 입력받으면 예외가 발생한다.")
    void updateReviewWithCafeTypeIndexOutOfRange() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequestMinus = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 0, 1, 1, "vibe");
        ReviewUpdateRequest reviewUpdateRequestOver = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 6, 1, 1, "vibe");

        // when // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, reviewUpdateRequestMinus))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, reviewUpdateRequestOver))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다.");
    }

    @Test
    @DisplayName("예외: 리뷰 수정 시 사진을 3개 보다 많이 등록 할 경우 도메인 정책상 예외가 발생한다.")
    void updateReviewWithImagesExceeded() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        List<String> newImageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", newImageUrls);

        // when // then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, reviewUpdateRequest))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("이미지는 최대 3개 까지 등록할 수 있습니다.");
    }

    @Test
    @DisplayName("경계: 리뷰 수정 시 간접적인 설문을 통해 까페의 타입 지수를 1 에서 5 로 매길 수 있다. 1 과 5 는 가능해야 한다.")
    void updateReviewWithCafeTypeIndexOutOfRangeBoundary() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용 수정", "아이스 라떼", 1, 1, 1, 5, "vibe");

        // when
        reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("cafeTypeInfo.cafeType")
                .contains(NOISY);
    }

    @Test
    @DisplayName("경계: 리뷰 수정 시 사진을 3개까지는 등록 할 수 있다.")
    void updateReviewWithMaxNumberOfImages() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        Long writerId = memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        List<String> newImageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        ReviewUpdateRequest reviewUpdateRequest = createReviewUpdateRequest(
                writerId, "일하거나 책읽고 공부하려고요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", newImageUrls);

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, reviewUpdateRequest);

        // then
        assertThat(response.getImageUrls()).hasSize(3)
                .containsExactlyInAnyOrder(
                        "https://storage.com/images/80459",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                );
    }

    @Test
    @DisplayName("성공: 회원은 자기가 작성한 리뷰를 삭제할 수 있다.")
    void deleteReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "넓어서 갔어요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        List<String> imageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> createReviewImage(imageUrl, review)).collect(Collectors.toList());

        Long reviewId = reviewRepository.save(review).getId();
        reviewImageRepository.saveAll(reviewImages);

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        // when
        reviewService.deleteReview(reviewId);

        // then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1)
                .extracting("id", "status")
                .contains(
                        tuple(reviewId, INACTIVE)
                );
    }

    @Test
    @DisplayName("성공: 리뷰 삭제 시 삭제할 리뷰로 인한 기록은 없었던 것으로 하고 카페 타입 및 테마를 갱신한다.")
    void deleteReviewWithUpdateCafeTypeAndCafeTheme() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "넓어서 갔어요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        Long reviewId = reviewRepository.save(review).getId();

        // TODO: 다른 행위를 끌어다 테스트 환경을 조성함
        cafe.updateCafeType(review);
        cafe.updateCafeTheme(review);
        cafeRepository.save(cafe);

        // when
        reviewService.deleteReview(reviewId);

        // then
        List<Cafe> cafes = cafeRepository.findAll();
        assertThat(cafes).hasSize(1)
                .extracting("cafeTypeInfo.cafeType", "cafeThemeInfo.cafeTheme")
                .contains(
                        tuple(NONE, ETC)
                )
                .doesNotContain(
                        tuple(SPACIOUS, VIBE)
                );
    }

    @Test
    @DisplayName("성공: 리뷰 삭제 시 리뷰 이미지도 함께 삭제한다.")
    void deleteReviewWithReviewImages() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer).getId();

        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url", cafeLocation);
        cafeRepository.save(cafe);

        LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Review review = createReview(
                writer, cafe, "넓어서 갔어요", "리뷰 내용", "아이스 아메리카노", 1, 4, 1, 1, "vibe", registeredAt);
        List<String> imageUrls = List.of(
                "https://storage.com/images/80459",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> createReviewImage(imageUrl, review)).collect(Collectors.toList());

        Long reviewId = reviewRepository.save(review).getId();
        reviewImageRepository.saveAll(reviewImages);

        // when
        reviewService.deleteReview(reviewId);

        // then
        List<ReviewImage> reviewImagesAll = reviewImageRepository.findAll();
        assertThat(reviewImagesAll).hasSize(3)
                .extracting("status")
                .contains(INACTIVE)
                .doesNotContain(ACTIVE);
    }

    @Test
    @DisplayName("예외: 리뷰 삭제 시 수정할 리뷰가 존재하지 않으면 예외가 발생한다.")
    void deleteNotExistReview() {
        // given
        Member writer = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(writer).getId();

        Long notExistReviewId = 1L;

        // when // then
        assertThatThrownBy(() -> reviewService.deleteReview(notExistReviewId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
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

    private Review createReview(Member writer, Cafe cafe, String visitPurpose,
                                String content, String menu, int coffeeIndex, int spaceIndex,
                                int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
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

    private LocationCreateRequest createLocationCreateRequest(
            double latitude, double longitude, String address, String roadAddress) {
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

    private ReviewUpdateRequest createReviewUpdateRequest(
            Long writerId, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme) {
        return ReviewUpdateRequest.builder()
                .writerId(writerId)
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

    private ReviewUpdateRequest createReviewUpdateRequest(
            Long writerId, String visitPurpose, String content, String menu,
            int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, List<String> imageUrls) {
        return ReviewUpdateRequest.builder()
                .writerId(writerId)
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