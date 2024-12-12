package com.capick.capick.dto.response;

import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.member.Profile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long id;

    private String email;

    private String nickname;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MemberProfileResponse profile;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocationResponse preferTown;

    @Builder
    private MemberResponse(
            Long id, String email, String nickname, MemberProfileResponse profile, LocationResponse location) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.preferTown = location;
    }

    public static MemberResponse of(Member member) {
        Profile profile = member.getProfile();
        Location preferTown = member.getPreferTown();
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile(
                        profile != null ? MemberProfileResponse.of(profile) : null
                )
                .location(
                        preferTown != null ? LocationResponse.of(preferTown) : null
                )
                .build();
    }

}
