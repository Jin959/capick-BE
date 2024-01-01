package com.capick.capick.service;

import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.member.Profile;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 방문자는 이메일, 비밀번호, 닉네임을 입력하고 가입할 수 있다.")
    void createMemberTest() {
        // given
        MemberCreateRequest request = createMemberRequest("email@naver.com", "password12^&*", "some_nickname");

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
        Member member = createMember("email01@naver.com", "password01%^&", "nickname01");
        memberRepository.save(member);
        MemberCreateRequest duplicateEmailRequest = createMemberRequest("email01@naver.com", "password02", "nickname02");
        MemberCreateRequest duplicateNicknameRequest = createMemberRequest("email02@naver.com", "password02", "nickname01");

        // when // then
        assertThatThrownBy(() -> memberService.createMember(duplicateEmailRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 존재하는 계정의 이메일 입니다.");
        assertThatThrownBy(() -> memberService.createMember(duplicateNicknameRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 사용 중인 닉네임 입니다.");
    }

    @Test
    @DisplayName("성공: 회원은 자신의 회원 정보를 조회 할 수 있다.")
    void getMember() {
        // given
        Profile profile = Profile.builder()
                .imageUrl("image URL")
                .introduction("자기소개 글")
                .build();
        Location preferTown = Location.builder()
                .latitude(48.8)
                .longitude(11.34)
                .city("뮌헨")
                .street("Marienplatz")
                .number("80331")
                .build();
        Profile profileOnlyIntro = Profile.builder()
                .introduction("자기소개 글")
                .build();

        Member memberRequiredOnly = createMember("email@naver.com", "password12^&*", "닉네임");
        Member memberWithProfile = createMember("email@naver.com", "password12^&*", "닉네임", profile);
        Member memberWithProfileAndPreferTown = createMember("email@naver.com", "password12^&*", "닉네임", profile, preferTown);
        Member memberWithIntro = createMember("email@naver.com", "password12^&*", "닉네임", profileOnlyIntro);
        memberRepository.saveAll(List.of(memberRequiredOnly, memberWithProfile, memberWithProfileAndPreferTown, memberWithIntro));

        // when
        MemberResponse response1 = memberService.getMember(memberRequiredOnly.getId());
        MemberResponse response2 = memberService.getMember(memberWithProfile.getId());
        MemberResponse response3 = memberService.getMember(memberWithProfileAndPreferTown.getId());
        MemberResponse response4 = memberService.getMember(memberWithIntro.getId());

        // then
        assertThat(response1)
                .extracting("id", "email", "nickname")
                .contains(memberRequiredOnly.getId(), "email@naver.com", "닉네임");
        assertThat(response2)
                .extracting("id", "email", "nickname",
                        "profile.imageUrl", "profile.introduction")
                .contains(memberWithProfile.getId(), "email@naver.com", "닉네임", "image URL", "자기소개 글");
        assertThat(response3)
                .extracting("id", "email", "nickname",
                        "profile.imageUrl", "profile.introduction",
                        "preferTown.latitude", "preferTown.longitude", "preferTown.city", "preferTown.street", "preferTown.number")
                .contains(memberWithProfileAndPreferTown.getId(), "email@naver.com", "닉네임", "image URL", "자기소개 글", 48.8, 11.34, "뮌헨", "Marienplatz", "80331");
        assertThat(response4)
                .extracting("id", "email", "nickname", "profile.introduction")
                .contains(memberWithIntro.getId(), "email@naver.com", "닉네임", "자기소개 글");
    }

    @Test
    @DisplayName("예외: 회원 탈퇴 처리가 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void getInactiveMember() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        member1.delete();
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        memberRepository.saveAll(List.of(member1, member2));
        Long deletedMemberId = 1L;
        Long notJoinedMemberId = 3L;

        // when // then
        assertThatThrownBy(() -> memberService.getMember(deletedMemberId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
        assertThatThrownBy(() -> memberService.getMember(notJoinedMemberId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    private Member createMember(String email, String password, String nickname) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private Member createMember(String email, String password, String nickname, Profile profile) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profile(profile)
                .build();
    }

    private Member createMember(String email, String password, String nickname, Profile profile, Location location) {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profile(profile)
                .location(location)
                .build();
    }

    private MemberCreateRequest createMemberRequest(String email, String password, String nickname) {
        return MemberCreateRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}