package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.request.ReviewCreateRequest;
import com.capick.capick.dto.request.ReviewUpdateRequest;
import com.capick.capick.dto.response.ReviewResponse;
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
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        LocalDateTime registeredAt = LocalDateTime.now();
        return ApiResponse.isCreated(reviewService.createReview(reviewCreateRequest, registeredAt));
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable("reviewId") Long reviewId) {
        return ApiResponse.ok(reviewService.getReview(reviewId));
    }

    @PatchMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable("reviewId") Long reviewId, @Valid @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ApiResponse.ok(reviewService.updateReview(reviewId, reviewUpdateRequest));
    }

}
