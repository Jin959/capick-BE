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
import java.util.stream.Stream;

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
        Review review = findReviewByIdOrElseThrow(reviewId);
        Member writer = findEditorWhoWroteOrElseThrow(reviewUpdateRequest.getWriterId(), review.getWriter().getId());

        Cafe cafe = review.getCafe();
        cafe.deductCafeTypeIndex(review);
        cafe.deductCafeThemeCount(review);

        review.updateReviewText(
                reviewUpdateRequest.getVisitPurpose(), reviewUpdateRequest.getContent(), reviewUpdateRequest.getMenu()
        );
        review.updateIndexes(
                reviewUpdateRequest.getCoffeeIndex(), reviewUpdateRequest.getSpaceIndex(),
                reviewUpdateRequest.getPriceIndex(), reviewUpdateRequest.getNoiseIndex()
        );
        review.updateTheme(reviewUpdateRequest.getTheme());
        Review updatedReview = reviewRepository.save(review);

        List<String> requestImageUrls = reviewUpdateRequest.getImageUrls();
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewAndStatus(review, ACTIVE);
        List<String> originalImageUrls = reviewImages.stream()
                .map(ReviewImage::getImageUrl).collect(Collectors.toList());

        List<ReviewImage> newReviewImages = createReviewImagesExcludingIntersection(
                requestImageUrls, originalImageUrls, review);
        List<ReviewImage> preservedReviewImages = extractIntersectionalReviewImagesDeletingDeprecated(
                originalImageUrls, requestImageUrls, reviewImages);
        reviewImageRepository.saveAll(newReviewImages);
        reviewImageRepository.saveAll(reviewImages);

        List<ReviewImage> updatedReviewImages = Stream
                .concat(newReviewImages.stream(), preservedReviewImages.stream()).collect(Collectors.toList());

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

    private Member findEditorWhoWroteOrElseThrow(Long editorId, Long writerId) {
        Member editor = memberServiceHelper.findMemberByIdOrElseThrow(editorId);
        if (!writerId.equals(editor.getId())) {
            throw UnauthorizedException.of(NOT_THE_WRITER);
        }
        return editor;
    }

    private List<ReviewImage> createReviewImagesExcludingIntersection(
            List<String> requestImageUrls, List<String> originalImageUrls, Review review) {
        List<String> newImageUrls = extractDifferenceSetOfImageUrls(requestImageUrls, originalImageUrls);
        return ReviewImage.createReviewImages(newImageUrls, review);
    }

    private List<ReviewImage> extractIntersectionalReviewImagesDeletingDeprecated(
            List<String> originalImageUrls, List<String> requestImageUrls, List<ReviewImage> reviewImages) {
        List<String> deprecatedImageUrls = extractDifferenceSetOfImageUrls(originalImageUrls, requestImageUrls);
        deleteDeprecatedReviewImages(deprecatedImageUrls, reviewImages);
        return reviewImages.stream()
                .filter(image -> image.getStatus().equals(ACTIVE)).collect(Collectors.toList());
    }

    private List<String> extractDifferenceSetOfImageUrls(List<String> ImageUrlsFrom, List<String> imageUrlsBy) {
        return ImageUrlsFrom.stream()
                .filter(url -> !imageUrlsBy.contains(url)).collect(Collectors.toList());
    }

    private void deleteDeprecatedReviewImages(List<String> deprecatedImageUrls, List<ReviewImage> reviewImages) {
        reviewImages.forEach(image -> {
            if (deprecatedImageUrls.contains(image.getImageUrl())) {
                image.delete();
            }
        });
    }
}
