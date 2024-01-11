package com.capick.capick.domain.member;

import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("성공: 회원 탈퇴 시 영구 삭제 전에 소프트 딜리트 된다.")
    void delete() {
        // given
        Member member = createMember("email@naver.com", "password123!*", "nickname12");

        // when
        member.delete();

        // then
        assertThat(member.getStatus()).isEqualByComparingTo(BaseStatus.INACTIVE);
    }

    @Test
    @DisplayName("성공: 넥네임을 수정할 수 있다.")
    void updateNickname() {
        // given
        Member member = createMember("email@naver.com", "pass!*word13", "nickname");

        // when
        member.updateNickname("new_nickname");

        // then
        assertThat(member)
                .extracting("email", "password", "nickname")
                .contains("email@naver.com", "pass!*word13", "new_nickname");
    }

    @Test
    @DisplayName("성공: 비밀번호를 수정할 수 있다.")
    void updatePassword() {
        // given
        Member member = createMember("email@naver.com", "pass!*word13", "nickname");

        // when
        member.updatePassword("new!*password12");

        // then
        assertThat(member)
                .extracting("email", "password", "nickname")
                .contains("email@naver.com", "new!*password12", "nickname");
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 이미 사용 중인 비밀번호로 변경하려할 경우 예외가 발생한다.")
    void updatePasswordWithUnchangedPassword() {
        // given
        Member member = createMember("email@naver.com", "pass!*word13", "nickname");
        String unchangedPassword = "pass!*word13";

        // when // then
        assertThatThrownBy(() -> member.updatePassword(unchangedPassword))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("현재와 동일한 비밀번호 입니다.");
    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}