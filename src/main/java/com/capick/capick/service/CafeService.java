package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.dto.PageResponse;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.dto.response.ReviewSimpleResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.NOT_FOUND_CAFE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;

    public CafeResponse getCafeByMapVendorPlaceId(String placeId) {
        Cafe cafe = cafeRepository.findByKakaoPlaceIdAndStatus(placeId, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_CAFE));
        return CafeResponse.of(cafe);
    }

    public PageResponse<ReviewSimpleResponse> getReviewsByCafeWithMapVendorPlaceId(String placeId, Pageable pageable) {
        return null;
    }
}
