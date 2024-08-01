package com.capick.capick.domain.cafe;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.review.Review;
import com.capick.capick.dto.request.LocationCreateRequest;
import com.capick.capick.exception.DomainLogicalException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

import static com.capick.capick.dto.ApiResponseStatus.FIRST_REVIEW_WITHOUT_CAFE_LOCATION;

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

    @Column(length = 50)
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

    // TODO: 지금 빌더로 location 을 받아서 지정할 수 있게 하였다. JPA 임베디드 타입은 공유참조에 주의해야 한다. 지금 외부에서 Location 을 대입 지정한다면 공유참조의 여지가 있다. 리팩토링하기
    @Builder
    private Cafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl, Location location) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
    }

    // TODO: DTO 인 CafeCreateRequest 를 인자로 받으려고 했으나 서비스 별로 다른 DTO 를 쓰면 다른 메서드 시그니쳐가 필요해질 수 있다. 전혀 다른 계층의 DTO를 추가할지 고민하기
    // TODO: LocationCreateRequest 는 toLocation 밖에 쓰지 않기 때문에 CafeCreateRequest 에 비해 변경의 전파가 상대적으로 작아서 그대로 사용했다. 이것도 다른 계층의 DTO 로 바꿀지 생각하기
    // TODO: 까페 위치 정보 수정 기능 updateLocation 을 개발해야 한다면 create 내부에서 호출하는 게 나을 것 같다. 도메인 정책상 까페 위치는 null 이면 안 되는데 개발자가 updateLocation 을 반드시 호출한다는 보장이 없기 때문이다.
    public static Cafe create(String name, String kakaoPlaceId, String kakaoDetailPageUrl, LocationCreateRequest location) {
        Optional.ofNullable(location)
                .orElseThrow(() -> DomainLogicalException.of(FIRST_REVIEW_WITHOUT_CAFE_LOCATION));

        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .location(location.toLocation())
                .build();
    }

    public void updateCafeType(Review review) {
        cafeTypeInfo.updateCafeType(review);
    }

    public void minusCafeTypeIndex(Review review) {
        cafeTypeInfo.minusCafeTypeIndex(review);
    }

    public void updateCafeTheme(Review review) {
        cafeThemeInfo.updateCafeTheme(review);
    }

    public void minusCafeThemeCount(Review review) {
        cafeThemeInfo.minusCafeThemeCount(review);
    }

}
