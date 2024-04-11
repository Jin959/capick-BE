package com.capick.capick.dto.request;

import lombok.Getter;

@Getter
public class LocationRequest {

    private Double latitude;

    private Double longitude;

    private String address;

    private String roadAddress;

}
