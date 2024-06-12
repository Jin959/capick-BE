package com.capick.capick.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CafeCreateRequest {

    @NotBlank(message = "카페 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "카페에 대한 외부 지도 서비스 상의 리소스 아이디를 입력해 주세요.")
    private String kakaoPlaceId;

    @Pattern(
            regexp = "((https?)://)?([\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥][%\\-_./&#?]?(\\?=)?)+",
            message = "카페 상세 페이지에 허용되지 않는 URL 입니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."
    )
    @Size(max = 50, message = "카페 상세 페이지 URL은 50자가 넘을 수 없습니다.")
    private String kakaoDetailPageUrl;

    private LocationCreateRequest location;

    @Builder
    public CafeCreateRequest(String name, String kakaoPlaceId,
                             String kakaoDetailPageUrl, LocationCreateRequest location) {
        this.name = name;
        this.kakaoPlaceId = kakaoPlaceId;
        this.kakaoDetailPageUrl = kakaoDetailPageUrl;
        this.location = location;
    }

}
