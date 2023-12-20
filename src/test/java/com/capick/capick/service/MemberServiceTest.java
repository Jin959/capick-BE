package com.capick.capick.service;

import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("방문자는 이메일, 비밀번호, 닉네임을 입력하고 가입할 수 있다.")
    void createMember() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .password("password")
                .nickname("some_nickname")
                .build();

        // when
        MemberCreateResponse response = memberService.createMember(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getNickname()).isEqualTo("some_nickname");
    }

}