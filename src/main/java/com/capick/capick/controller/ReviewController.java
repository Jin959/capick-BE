package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.request.ReviewUpdateRequest;
import com.capick.capick.dto.response.ReviewDetailResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
import com.capick.capick.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/new")
    public ApiResponse<ReviewSimpleResponse> createReview(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        LocalDateTime registeredAt = LocalDateTime.now();
        return ApiResponse.isCreated(reviewService.createReview(reviewCreateRequest, registeredAt));
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewSimpleResponse> getReview(@PathVariable("reviewId") Long reviewId) {
        return ApiResponse.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/{reviewId}/detail")
    public ApiResponse<ReviewDetailResponse> getReviewDetail(@PathVariable("reviewId") Long reviewId) {
        return ApiResponse.ok(reviewService.getReviewDetail(reviewId));
    }

    @PatchMapping("/{reviewId}")
    public ApiResponse<ReviewSimpleResponse> updateReview(
            @PathVariable("reviewId") Long reviewId, @Valid @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ApiResponse.ok(reviewService.updateReview(reviewId, reviewUpdateRequest));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.isDeleted();
    }

}
