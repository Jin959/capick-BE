package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    @GetMapping("map-vendors/kakao/{mapVendorPlaceId}")
    public ApiResponse<CafeResponse> getCafeByMapVendorPlaceId(@PathVariable("mapVendorPlaceId") String mapVendorPlaceId) {
        return ApiResponse.ok(cafeService.getCafeByMapVendorPlaceId(mapVendorPlaceId));
    }

}
