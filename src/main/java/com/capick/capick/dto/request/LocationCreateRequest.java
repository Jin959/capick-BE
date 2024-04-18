package com.capick.capick.dto.request;

import lombok.Getter;

@Getter
public class LocationCreateRequest {

    private Double latitude;

    private Double longitude;

    private String address;

    private String roadAddress;

}
