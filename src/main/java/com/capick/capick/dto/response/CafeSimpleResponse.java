package com.capick.capick.dto.response;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.cafe.CafeTheme;
import com.capick.capick.domain.cafe.CafeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CafeSimpleResponse {

    private String name;

    private String kakaoPlaceId;

    private CafeType cafeType;

    private CafeTheme cafeTheme;

    @Builder
    private CafeSimpleResponse(String name, String kakaoPlaceId, CafeType cafeType, CafeTheme cafeTheme) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.cafeType = cafeType;
        this.cafeTheme = cafeTheme;
    }

    public static CafeSimpleResponse of(Cafe cafe) {
        return CafeSimpleResponse.builder()
                .name(cafe.getName())
                .kakaoPlaceId(cafe.getKakaoPlaceId())
                .cafeType(cafe.getCafeTypeInfo().getCafeType())
                .cafeTheme(cafe.getCafeThemeInfo().getCafeTheme())
                .build();
    }

}
