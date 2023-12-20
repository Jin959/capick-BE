package com.capick.capick.dto.request;

import com.capick.capick.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    private String email;

    private String password;

    private String nickname;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}
