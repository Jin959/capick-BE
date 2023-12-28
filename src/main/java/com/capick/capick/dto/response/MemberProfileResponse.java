package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Profile;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberProfileResponse {

    private String imageUrl;

    private String introduction;

    @Builder
    private MemberProfileResponse(String imageUrl, String introduction) {
        this.imageUrl = imageUrl;
        this.introduction = introduction;
    }

    public static MemberProfileResponse of(Profile profile) {

        return MemberProfileResponse.builder()
                .imageUrl(profile.getImageUrl())
                .introduction(profile.getIntroduction())
                .build();
    }

}