package com.capick.capick.dto.response;

import com.capick.capick.domain.common.Location;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LocationResponse {

    private Double latitude;

    private Double longitude;

    private String address;

    private String roadAddress;

    @Builder
    private LocationResponse(Double latitude, Double longitude, String address, String roadAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.roadAddress = roadAddress;
    }

    public static LocationResponse of(Location location) {
        return LocationResponse.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .roadAddress(location.getRoadAddress())
                .build();
    }
}
