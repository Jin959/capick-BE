package com.capick.capick.repository;

import com.capick.capick.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("성공: 이미 사용 중인 이메일인지 조회한다.")
    void existsByEmailAndStatus() {
        // given
        Member member = createMember("email01@naver.com", "password01", "member1");
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByEmailAndStatus("email01@naver.com", ACTIVE);
        boolean notExists = memberRepository.existsByEmailAndStatus("email02@naver.com", ACTIVE);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("성공: 이미 사용 중인 닉네임인지 조회한다.")
    void existsByNicknameAndStatus() {
        // given
        Member member = createMember("email01@naver.com", "password01", "member1");
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByNicknameAndStatus("member1", ACTIVE);
        boolean notExists = memberRepository.existsByNicknameAndStatus("member2", ACTIVE);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    private Member createMember(String email, String password, String memeber) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(memeber)
                .build();
    }

}