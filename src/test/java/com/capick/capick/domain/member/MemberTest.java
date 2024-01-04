package com.capick.capick.domain.member;

import com.capick.capick.domain.common.BaseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("성공: 회원 정보 수정 시 입력된 값에 대해서 수정한다.")
    void updateInfo() {
        // given
        Member member1 = createMember("email1@naver.com", "password123!*", "nickname1");
        Member member2 = createMember("email2@naver.com", "password123!*", "nickname2");
        Member member3 = createMember("email3@naver.com", "password123!*", "nickname3");

        // when
        member1.updateInfo("new_password123!*", null);
        member2.updateInfo(null, "new_nickname");
        member3.updateInfo("new_password123!*", "new_nickname");

        // then
        assertThat(member1)
                .extracting("email", "password", "nickname")
                .contains("email1@naver.com", "new_password123!*", "nickname1");
        assertThat(member2)
                .extracting("email", "password", "nickname")
                .contains("email2@naver.com", "password123!*", "new_nickname");
        assertThat(member3)
                .extracting("email", "password", "nickname")
                .contains("email3@naver.com", "new_password123!*", "new_nickname");
    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}