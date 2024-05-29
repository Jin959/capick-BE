package com.capick.capick.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CafeCreateRequest {

    private String name;

    private String kakaoPlaceId;

    private String kakaoDetailPageUrl;

    private LocationCreateRequest location;

    @Builder
    public CafeCreateRequest(String name, String kakaoPlaceId,
                             String kakaoDetailPageUrl, LocationCreateRequest location) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
    }

}
