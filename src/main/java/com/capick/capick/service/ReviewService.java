package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.DomainPoliticalArgumentException;
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
import static com.capick.capick.dto.ApiResponseStatus.NUMBER_OF_REVIEW_IMAGE_EXCEEDED;

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
        Cafe cafe = cafeRepository.findByKakaoPlaceIdAndStatus(cafeCreateRequest.getKakaoPlaceId(), ACTIVE)
                .orElseGet(() -> Cafe.create(
                            cafeCreateRequest.getName(), cafeCreateRequest.getKakaoPlaceId(),
                            cafeCreateRequest.getKakaoDetailPageUrl(), cafeCreateRequest.getLocation()
                    ));

        Review review = reviewCreateRequest.toEntity(writer, cafe, registeredAt);
        review.updateIndexes(
                reviewCreateRequest.getCoffeeIndex(), reviewCreateRequest.getSpaceIndex(),
                reviewCreateRequest.getPriceIndex(), reviewCreateRequest.getNoiseIndex());
        Review savedReview = reviewRepository.save(review);

        List<String> imageUrls = reviewCreateRequest.getImageUrls();
        if (imageUrls.size() > 3) {
            throw DomainPoliticalArgumentException.of(NUMBER_OF_REVIEW_IMAGE_EXCEEDED);
        }
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> ReviewImage.create(imageUrl, savedReview)).collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImages);

        cafe.updateCafeType(savedReview);
        cafe.updateCafeTheme(savedReview);
        cafeRepository.save(cafe);

        return ReviewResponse.of(savedReview, reviewImages);
    }
}
