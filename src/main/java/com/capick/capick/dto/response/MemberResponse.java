package com.capick.capick.dto.response;

import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.member.Profile;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class MemberResponse {

    private Long id;

    private String email;

    private String nickname;

    private MemberProfileResponse profile;

    private LocationResponse preferTown;

    @Builder
    private MemberResponse(Long id, String email, String nickname, Profile profile, Location location) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        Optional.ofNullable(profile)
                .ifPresent(embeddedProfile -> this.profile = MemberProfileResponse.of(embeddedProfile));
        Optional.ofNullable(location)
                .ifPresent(embeddedLocation -> this.preferTown = LocationResponse.of(embeddedLocation));
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile(member.getProfile())
                .location(member.getPreferTown())
                .build();
    }

}
