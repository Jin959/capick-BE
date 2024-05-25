package com.capick.capick.dto.request;

import com.capick.capick.domain.cafe.Cafe;
import lombok.Builder;
import lombok.Getter;

@Getter
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

    public Cafe toEntity() {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .location(location.toLocation())
                .build();
    }

}
