package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.request.ReviewUpdateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.exception.UnauthorizedException;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.ReviewImageRepository;
import com.capick.capick.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.NOT_FOUND_REVIEW;
import static com.capick.capick.dto.ApiResponseStatus.NOT_THE_WRITER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final CafeRepository cafeRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final MemberServiceHelper memberServiceHelper;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest, LocalDateTime registeredAt) {
        Member writer = memberServiceHelper.findMemberByIdOrElseThrow(reviewCreateRequest.getWriterId());

        CafeCreateRequest cafeCreateRequest = reviewCreateRequest.getCafe();
        Cafe cafe = findCafeByKakakoPlaceIdOrElseCreate(cafeCreateRequest);

        Review review = reviewCreateRequest.toEntity(writer, cafe, registeredAt);
        review.updateIndexes(
                reviewCreateRequest.getCoffeeIndex(), reviewCreateRequest.getSpaceIndex(),
                reviewCreateRequest.getPriceIndex(), reviewCreateRequest.getNoiseIndex());
        Review savedReview = reviewRepository.save(review);

        List<ReviewImage> reviewImages = ReviewImage.createReviewImages(reviewCreateRequest.getImageUrls(), savedReview);
        reviewImageRepository.saveAll(reviewImages);

        cafe.updateCafeType(savedReview);
        cafe.updateCafeTheme(savedReview);
        cafeRepository.save(cafe);

        return ReviewResponse.of(savedReview, reviewImages, writer);
    }

    public ReviewResponse getReview(Long reviewId) {
        Review review = findReviewByIdOrElseThrow(reviewId);
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewAndStatus(review, ACTIVE);
        return ReviewResponse.of(review, reviewImages, review.getWriter());
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        // 작성자 검증
        Review review = findReviewByIdOrElseThrow(reviewId);
        Member writer = memberServiceHelper.findMemberByIdOrElseThrow(reviewUpdateRequest.getWriterId());
        if (!review.getWriter().getId().equals(writer.getId())) {
            throw UnauthorizedException.of(NOT_THE_WRITER);
        }

        // 카페 타입 지수, 테마 누적 횟수 마이너스
        Cafe cafe = review.getCafe();
        cafe.deductCafeTypeIndex(review);
        cafe.deductCafeThemeCount(review);

        // 리뷰 내용 업데이트
        review.updateReviewText(
                reviewUpdateRequest.getVisitPurpose(), reviewUpdateRequest.getContent(), reviewUpdateRequest.getMenu()
        );
        review.updateIndexes(
                reviewUpdateRequest.getCoffeeIndex(), reviewUpdateRequest.getSpaceIndex(),
                reviewUpdateRequest.getPriceIndex(), reviewUpdateRequest.getNoiseIndex()
        );
        review.updateTheme(reviewUpdateRequest.getTheme());
        Review updatedReview = reviewRepository.save(review);

        // 리뷰 이미지 업데이트 도메인 로직
        List<String> requestImageUrls = reviewUpdateRequest.getImageUrls();
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewAndStatus(review, ACTIVE);

        List<String> originalImageUrls = reviewImages.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList());
        // 이전에 없던 것들은 엔터티 생성
        List<String> newImageUrls = requestImageUrls.stream().filter(url -> !originalImageUrls.contains(url)).collect(Collectors.toList());
        List<ReviewImage> updatedReviewImages = ReviewImage.createReviewImages(newImageUrls, review);
        reviewImageRepository.saveAll(updatedReviewImages);
        // 원래 있던 이미지들 중 요청 된 이미지와 겹치지 않는 것은 엔터티 제거
        List<String> deprecatedImageUrls = originalImageUrls.stream().filter(url -> !requestImageUrls.contains(url)).collect(Collectors.toList());
        reviewImages.stream().forEach(image -> {
            if (deprecatedImageUrls.contains(image.getImageUrl())) {
                image.delete();
            } else {
                updatedReviewImages.add(image);
            }
        });
        reviewImageRepository.saveAll(reviewImages);

        // 카페 타입, 테마 업데이트
        cafe.updateCafeType(updatedReview);
        cafe.updateCafeTheme(updatedReview);
        cafeRepository.save(cafe);

        return ReviewResponse.of(updatedReview, updatedReviewImages, writer);
    }

    private Review findReviewByIdOrElseThrow(Long reviewId) {
        return reviewRepository.findByIdAndStatus(reviewId, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_REVIEW));
    }

    private Cafe findCafeByKakakoPlaceIdOrElseCreate(CafeCreateRequest cafeCreateRequest) {
        return cafeRepository.findByKakaoPlaceIdAndStatus(cafeCreateRequest.getKakaoPlaceId(), ACTIVE)
                .orElseGet(() -> Cafe.create(
                        cafeCreateRequest.getName(), cafeCreateRequest.getKakaoPlaceId(),
                        cafeCreateRequest.getKakaoDetailPageUrl(), cafeCreateRequest.getLocation()
                ));
    }
}
