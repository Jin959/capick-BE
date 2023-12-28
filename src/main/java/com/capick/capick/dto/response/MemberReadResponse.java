package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberReadResponse {

    private Long id;

    private String email;

    private String nickname;

    private MemberProfileResponse profile;

    private LocationResponse preferTown;

    @Builder
    private MemberReadResponse(Long id, String email, String nickname, MemberProfileResponse profile, LocationResponse location) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.preferTown = location;
    }

    public static MemberReadResponse of(Member member) {
        return MemberReadResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile(MemberProfileResponse.of(member.getProfile()))
                .location(LocationResponse.of(member.getPreferTown()))
                .build();
    }

}
