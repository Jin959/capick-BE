package com.capick.capick.dto.response;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.cafe.CafeTheme;
import com.capick.capick.domain.cafe.CafeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CafeResponse {

    private String name;

    private String kakaoPlaceId;

    private String kakaoDetailPageUrl;

    private LocationResponse location;

    private CafeType cafeType;

    private CafeTheme cafeTheme;

    @Builder
    private CafeResponse(
            String name, String kakaoPlaceId, String kakaoDetailPageUrl,
            LocationResponse location, CafeType cafeType, CafeTheme cafeTheme) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
        this.cafeType = cafeType;
        this.cafeTheme = cafeTheme;
    }

    public static CafeResponse of(Cafe cafe) {
        return CafeResponse.builder()
                .name(cafe.getName())
                .kakaoPlaceId(cafe.getKakaoPlaceId())
                .kakaoDetailPageUrl(cafe.getKakaoDetailPageUrl())
                .location(
                        LocationResponse.of(cafe.getLocation())
                )
                .cafeType(cafe.getCafeTypeInfo().getCafeType())
                .cafeTheme(cafe.getCafeThemeInfo().getCafeTheme())
                .build();
    }

}
