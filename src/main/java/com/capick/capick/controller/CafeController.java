package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.PageResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
import com.capick.capick.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    @GetMapping("/kakao/{placeId}")
    public ApiResponse<CafeResponse> getCafeByMapVendorPlaceId(@PathVariable("placeId") String placeId) {
        return ApiResponse.ok(cafeService.getCafeByMapVendorPlaceId(placeId));
    }

    @GetMapping("/kakao/{placeId}/reivews")
    public ApiResponse<PageResponse<ReviewSimpleResponse>> getReviewsByCafeWithMapVendorPlaceId(
            @PathVariable("placeId") String placeId,
            @PageableDefault(sort = "registeredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(cafeService.getReviewsByCafeWithMapVendorPlaceId(placeId, pageable));
    }

}
