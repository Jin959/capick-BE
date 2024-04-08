package com.capick.capick.domain.cafe;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cafe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kakaoPlaceId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "location_address")),
            @AttributeOverride(name = "roadAddress", column = @Column(name = "location_road_address")),
    })
    private Location location;

    @Enumerated(EnumType.STRING)
    private CafeType cafeType;

}
