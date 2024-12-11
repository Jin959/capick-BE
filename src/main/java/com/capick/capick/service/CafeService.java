package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.capick.capick.dto.PageResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.CafeRepository;
import com.capick.capick.repository.ReviewImageRepository;
import com.capick.capick.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.NOT_FOUND_CAFE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewImageRepository reviewImageRepository;

    public CafeResponse getCafeByMapVendorPlaceId(String placeId) {
        Cafe cafe = cafeRepository.findByKakaoPlaceIdAndStatus(placeId, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_CAFE));
        return CafeResponse.of(cafe);
    }

    public PageResponse<ReviewSimpleResponse> getReviewsByCafeWithMapVendorPlaceId(String placeId, Pageable pageable) {
        Cafe cafe = cafeRepository.findByKakaoPlaceIdAndStatus(placeId, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_CAFE));

        Page<Review> reviewPage = reviewRepository.findPageByCafeAndStatus(cafe, ACTIVE, pageable);

        List<ReviewImage> thumbnails = reviewImageRepository.findReviewThumbnailsBy(reviewPage.getContent(), ACTIVE);
        Map<Long, ReviewImage> thumbnailMap = thumbnails.stream()
                .collect(Collectors.toMap(thumbnail -> thumbnail.getReview().getId(), thumbnail -> thumbnail));

        return PageResponse.of(reviewPage.map(review ->
                ReviewSimpleResponse.of(review, thumbnailMap.get(review.getId()), cafe)
        ));
    }

}
