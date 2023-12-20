package com.capick.capick.dto.request;

import com.capick.capick.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    private String email;

    private String password;

    private String nickname;

    @Builder
    public MemberCreateRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}
