package com.capick.capick.dto.request;

import lombok.Getter;

@Getter
public class CafeCreateRequest {

    private String name;

    private String kakaoPlaceId;

    private String kakaoDetailPageUrl;

    private LocationCreateRequest location;

}
