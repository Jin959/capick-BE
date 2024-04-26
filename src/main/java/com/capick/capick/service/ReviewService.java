package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
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

    private final MemberServiceHelper memberServiceHelper;

    public ReviewResponse createReview(ReviewCreateRequest reviewCreateRequest) {
        Member writer = memberServiceHelper.findMemberByIdOrElseThrow(reviewCreateRequest.getWriterId());
        Cafe cafe = Cafe.create();
        Review review = reviewRepository.save(reviewCreateRequest.toEntity(writer, cafe));
        cafe.updateCafeType(review);
        return ReviewResponse.of(review);
    }
}
