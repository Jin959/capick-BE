package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreateResponse {

    private Long id;

    private String nickname;

    @Builder
    private MemberCreateResponse(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public static MemberCreateResponse of(Member member) {
        return MemberCreateResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .build();
    }

}
