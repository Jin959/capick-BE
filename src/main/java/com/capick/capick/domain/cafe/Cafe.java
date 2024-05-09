package com.capick.capick.domain.cafe;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.review.Review;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false, length = 30)
    private String name;

    private String kakaoPlaceId;

    @Column(length = 30)
    private String kakaoDetailPageUrl;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "location_address")),
            @AttributeOverride(name = "roadAddress", column = @Column(name = "location_road_address")),
    })
    private Location location;

    @Embedded
    private CafeTypeInfo cafeTypeInfo = new CafeTypeInfo(0, 0, 0, 0, CafeType.NONE);

    @Embedded
    private CafeThemeInfo cafeThemeInfo = new CafeThemeInfo(0, 0, 0, 0, 0, 0, 0, 0, CafeTheme.NORMAL);

    @Builder
    private Cafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl, Location location) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
    }

    // TODO: 리뷰 생성 DTO 개발 후 인자로 받아서 마저 개발
    public static Cafe create() {
        return Cafe.builder()
                .build();
    }

    public void updateCafeType(Review review) {
        cafeTypeInfo.updateCafeType(review);
    }

    public void updateCafeTheme(Review review) {
        cafeThemeInfo.updateCafeTheme(review);
    }

}
