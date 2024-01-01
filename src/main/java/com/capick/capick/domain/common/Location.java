package com.capick.capick.domain.common;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    private Double latitude;

    private Double longitude;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String street;

    @Column(length = 10)
    private String number;

}
