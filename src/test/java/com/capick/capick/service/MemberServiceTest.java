package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import com.capick.capick.exception.BaseException;
import com.capick.capick.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("성공: 방문자는 이메일, 비밀번호, 닉네임을 입력하고 가입할 수 있다.")
    void createMember() {
        // given
        MemberCreateRequest request = createMemberRequest("email@naver.com", "password", "some_nickname");

        // when
        MemberCreateResponse response = memberService.createMember(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getNickname()).isEqualTo("some_nickname");
    }

    @Test
    @DisplayName("예외: 동일한 이메일, 닉네임이 이미 서비스에 존재하면 안된다. 중복된 이메일 또는 닉네임으로 가입하는 경우 예외가 발생한다.")
    void createMemberWithDuplicateEmail() {
        // given
        Member member = Member.builder()
                .email("email01@naver.com")
                .password("password01")
                .nickname("nickname01")
                .build();
        memberRepository.save(member);
        MemberCreateRequest duplicateEmailRequest = createMemberRequest("email01@naver.com", "password02", "nickname02");
        MemberCreateRequest duplicateNicknameRequest = createMemberRequest("email02@naver.com", "password02", "nickname01");

        // when // then
        assertThatThrownBy(() -> memberService.createMember(duplicateEmailRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("이미 존재하는 계정의 이메일 입니다.");
        assertThatThrownBy(() -> memberService.createMember(duplicateNicknameRequest))
                .isInstanceOf(BaseException.class)
                .hasMessage("이미 사용 중인 닉네임 입니다.");
    }

    private MemberCreateRequest createMemberRequest(String email, String password, String nickname) {
        return MemberCreateRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}