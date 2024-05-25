package com.capick.capick.dto.request;

import com.capick.capick.domain.common.Location;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LocationCreateRequest {

    private Double latitude;

    private Double longitude;

    private String address;

    private String roadAddress;

    @Builder
    public LocationCreateRequest(Double latitude, Double longitude, String address, String roadAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.roadAddress = roadAddress;
    }

    public Location toLocation() {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .roadAddress(roadAddress)
                .build();
    }

}
