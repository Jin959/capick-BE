package com.capick.capick.dto.request;

import lombok.Getter;

@Getter
public class MemberCreateRequest {

    private String email;

    private String password;

    private String nickname;

}
