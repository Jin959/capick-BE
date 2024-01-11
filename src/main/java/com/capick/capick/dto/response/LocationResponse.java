package com.capick.capick.dto.response;

import com.capick.capick.domain.common.Location;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LocationResponse {

    private Double latitude;

    private Double longitude;

    private String state;

    private String city;

    private String street;

    private String number;

    @Builder
    private LocationResponse(Double latitude, Double longitude, String state, String city, String street, String number) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.city = city;
        this.street = street;
        this.number = number;
    }

    public static LocationResponse of(Location location) {
        return LocationResponse.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .state(location.getState())
                .city(location.getCity())
                .street(location.getStreet())
                .number(location.getNumber())
                .build();
    }
}
