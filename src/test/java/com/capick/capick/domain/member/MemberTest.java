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
        Member member = Member.builder()
                .email("email@naver.com")
                .password("password123!*")
                .nickname("nickname12")
                .build();

        // when
        member.delete();

        // then
        assertThat(member.getStatus()).isEqualByComparingTo(BaseStatus.INACTIVE);
    }

}