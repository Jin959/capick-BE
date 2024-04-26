package com.capick.capick.domain.cafe;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.review.Review;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

    @Column(nullable = false)
    private int coffeeIndex;

    @Column(nullable = false)
    private int priceIndex;

    @Column(nullable = false)
    private int spaceIndex;

    @Column(nullable = false)
    private int noiseIndex;

    @Enumerated(EnumType.STRING)
    private CafeType cafeType;

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
        this.updateIndexes(review);

        Map<String, Integer> indexMap = new HashMap<>();
        indexMap.put("coffeeIndex", this.coffeeIndex);
        indexMap.put("spaceIndex", this.spaceIndex);
        indexMap.put("priceIndex", this.priceIndex);
        indexMap.put("noiseIndex", this.noiseIndex);

        // TODO: 최대값 반환 로직 메서드 추출하기, 가독성 측면, extractMaxIndexName
        String maxIndexName = indexMap.keySet().stream()
                .max(Comparator.comparing(indexMap::get))
                .orElseGet(() -> "none");

        // TODO: findeByIndexName 으로 분리하기, enum 에 책임이 있는지 고민해보기
        this.cafeType = Arrays.stream(CafeType.values())
                .filter(cafeType -> cafeType.getIndexName().equals(maxIndexName))
                .findFirst()
                .orElseGet(() -> CafeType.NONE);
    }

    private void updateIndexes(Review review) {
        this.coffeeIndex += review.getCoffeeIndex();
        this.spaceIndex += review.getSpaceIndex();
        this.priceIndex += review.getPriceIndex();
        this.noiseIndex += review.getNoiseIndex();
    }

}
