package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Profile;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class MemberProfileResponse {

    private String imageUrl;

    private String introduction;

    @Builder
    private MemberProfileResponse(String imageUrl, String introduction) {
        Optional.ofNullable(imageUrl)
                .ifPresent(url -> this.imageUrl = url);
        Optional.ofNullable(introduction)
                .ifPresent(intro -> this.introduction = intro);
    }

    public static MemberProfileResponse of(Profile profile) {

        return MemberProfileResponse.builder()
                .imageUrl(profile.getImageUrl())
                .introduction(profile.getIntroduction())
                .build();
    }

}
