package com.capick.capick.service;

import com.capick.capick.domain.review.Review;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.response.ReviewResponse;
import com.capick.capick.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest) {
        Review review = reviewRepository.save(reviewCreateRequest.toEntity());
        return ReviewResponse.of(review);
    }
}
