package com.capick.capick.service;

import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;

    public CafeResponse getCafeByMapVendorPlaceId(String mapVendorPlaceId) {
        return null;
    }

}
