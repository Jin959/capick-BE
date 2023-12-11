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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude")),
            @AttributeOverride(name = "state", column = @Column(name = "location_state")),
            @AttributeOverride(name = "city", column = @Column(name = "location_city")),
            @AttributeOverride(name = "street", column = @Column(name = "location_street")),
            @AttributeOverride(name = "number", column = @Column(name = "location_number"))
    })
    private Location location;

    @Enumerated(EnumType.STRING)
    private CafeType cafeType;

}
