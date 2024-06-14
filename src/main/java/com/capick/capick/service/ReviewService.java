package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.ReviewImageRepository;
import com.capick.capick.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.NOT_FOUND_REVIEW;

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
        Review review = reviewRepository.findByIdAndStatus(reviewId, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_REVIEW));
        return ReviewResponse.of(review, review.getReviewImages(), review.getWriter());
    }

    private Cafe findCafeByKakakoPlaceIdOrElseCreate(CafeCreateRequest cafeCreateRequest) {
        return cafeRepository.findByKakaoPlaceIdAndStatus(cafeCreateRequest.getKakaoPlaceId(), ACTIVE)
                .orElseGet(() -> Cafe.create(
                        cafeCreateRequest.getName(), cafeCreateRequest.getKakaoPlaceId(),
                        cafeCreateRequest.getKakaoDetailPageUrl(), cafeCreateRequest.getLocation()
                ));
    }
}
