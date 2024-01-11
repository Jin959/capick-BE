package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSimpleResponse {

    private Long id;

    private String nickname;

    @Builder
    private MemberSimpleResponse(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public static MemberSimpleResponse of(Member member) {
        return MemberSimpleResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .build();
    }

}
