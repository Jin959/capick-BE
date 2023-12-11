package com.capick.capick.domain.member;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Embedded
    private Profile profile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "prefer_town_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "prefer_town_longitude")),
            @AttributeOverride(name = "state", column = @Column(name = "prefer_town_state")),
            @AttributeOverride(name = "city", column = @Column(name = "prefer_town_city")),
            @AttributeOverride(name = "street", column = @Column(name = "prefer_town_street")),
            @AttributeOverride(name = "number", column = @Column(name = "prefer_town_number"))
    })
    private Location preferTown;

}
