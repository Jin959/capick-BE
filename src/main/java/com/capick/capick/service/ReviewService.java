package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.dto.request.CafeCreateRequest;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.exception.DomainLogicalException;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.FIRST_REVIEW_WITHOUT_CAFE_LOCATION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final CafeRepository cafeRepository;

    private final MemberServiceHelper memberServiceHelper;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest) {
        Member writer = memberServiceHelper.findMemberByIdOrElseThrow(reviewCreateRequest.getWriterId());

        CafeCreateRequest cafeCreateRequest = reviewCreateRequest.getCafe();
        Cafe cafe = cafeRepository.findByKakaoPlaceIdAndStatus(cafeCreateRequest.getKakaoPlaceId(), ACTIVE)
                .orElseGet(() -> {
                    Optional.ofNullable(cafeCreateRequest.getLocation())
                            .orElseThrow(() -> DomainLogicalException.of(FIRST_REVIEW_WITHOUT_CAFE_LOCATION));
                    return Cafe.create();
                });

        Review review = reviewCreateRequest.toEntity(writer, cafe);
        review.updateIndexes(
                reviewCreateRequest.getCoffeeIndex(), reviewCreateRequest.getSpaceIndex(),
                reviewCreateRequest.getPriceIndex(), reviewCreateRequest.getNoiseIndex());
        Review savedReview = reviewRepository.save(review);

        cafe.updateCafeType(savedReview);
        cafe.updateCafeTheme(savedReview);
        cafeRepository.save(cafe);

        return ReviewResponse.of(savedReview);
    }
}
