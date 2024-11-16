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

    // TODO: 지금 빌더로 location 을 받아서 지정할 수 있게 하였다. JPA 임베디드 타입은 공유참조에 주의해야 한다.
    //  지금 외부에서 Location 을 생성하여 대입 지정한다면 공유참조의 여지가 있다. 리팩토링하기
    @Builder
    private Cafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl, Location location) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
    }

    // TODO: DTO 인 CafeCreateRequest 를 인자로 받으려고 했으나 추후에 비즈니스 로직별로 다른 DTO 를 쓰면 다른 메서드 시그니쳐가 필요해질 수 있다.
    //  전혀 다른 계층의 DTO를 추가할지 고민하기
    // TODO: LocationCreateRequest 는 여러 서비스 계층에서 사용한다고 해도, 필드를 따로 꺼내서 다른 객체에 전달할 일이 없다. 카페 생성과 프로필 생성에만 쓰이기 때문이다.
    //  대부분의 로직에서 한다는 행위가 toLocation 밖에 없다. CafeCreateRequest 같은 DTO 에 비하면 변경이 일어날 일도 거의 없다. 그래서 그대로 인자로 받았다.
    //  게다가, Cafe 엔터티 외부에서 LocationCreateRequest 를 임베디드 타입 Location 으로 변환하여 전달 받으면
    //  LocationCreateRequest == null 일 때, 외부에서 NPE 우려도 있고, 도메인 예외를 서비스 계층에서 처리해야 한다.
    //  이것도 다른 계층의 DTO 로 바꿀지 생각하기
    // TODO: Cafe.updateLocation 을 개발하여 Cafe.create 에서 위치 정보 기록행위를 분리한다면 Cafe.create 내부에서 Cafe.updateLocation 을 호출하는 게 나을 것 같다.
    //  도메인 정책상 카페 위치는 null 이면 안 되는데 개발자가 updateLocation 을 비즈니스 로직 전개 시 반드시 호출한다는 보장이 없기 때문이다.
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

    public void updateCafeTypeByAdding(Review review) {
        cafeTypeInfo.addCafeTypeIndexes(review);
        cafeTypeInfo.ifHasMaxIndexUpdateCafeType();
    }

    public void updateCafeTypeByDeducting(Review review) {
        cafeTypeInfo.deductCafeTypeIndexes(review);
        cafeTypeInfo.ifHasMaxIndexUpdateCafeType();
    }

    public void updateCafeThemeByAdding(Review review) {
        cafeThemeInfo.addCafeThemeCount(review);
        cafeThemeInfo.ifHasMaxThemeCountUpdateCafeTheme();
    }

    public void updateCafeThemeByDeducting(Review review) {
        cafeThemeInfo.deductCafeThemeCount(review);
        cafeThemeInfo.ifHasMaxThemeCountUpdateCafeTheme();
    }

}
