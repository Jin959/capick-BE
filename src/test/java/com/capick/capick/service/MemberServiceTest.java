package com.capick.capick.service;

import com.capick.capick.domain.common.Location;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.member.Profile;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberNicknameRequest;
import com.capick.capick.dto.request.MemberPasswordRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.exception.UnauthorizedException;
import com.capick.capick.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.domain.common.BaseStatus.INACTIVE;
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
        MemberCreateRequest request = createMemberCreateRequest("email@naver.com", "password12^&*", "some_nickname");

        // when
        MemberSimpleResponse response = memberService.createMember(request);

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
        MemberCreateRequest duplicateEmailRequest = createMemberCreateRequest("email01@naver.com", "password02", "nickname02");
        MemberCreateRequest duplicateNicknameRequest = createMemberCreateRequest("email02@naver.com", "password02", "nickname01");

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
                .address("독일 뮌헨")
                .roadAddress("독일 뮌헨로")
                .build();
        Profile profileOnlyIntro = Profile.builder()
                .introduction("자기소개 글")
                .build();

        Member memberRequiredOnly = createMember("email@naver.com", "password12^&*", "닉네임");
        Member memberWithProfile = createMember("email@naver.com", "password12^&*", "닉네임", profile);
        Member memberWithProfileAndPreferTown = createMember("email@naver.com", "password12^&*", "닉네임", profile, preferTown);
        Member memberWithIntro = createMember("email@naver.com", "password12^&*", "닉네임", profileOnlyIntro);
        List<Long> memberIds = memberRepository.saveAll(List.of(memberRequiredOnly, memberWithProfile, memberWithProfileAndPreferTown, memberWithIntro))
                .stream().map(Member::getId).collect(Collectors.toList());

        // when
        List<MemberResponse> responses = memberIds.stream()
                .map(memberId -> memberService.getMember(memberId)).collect(Collectors.toList());

        // then
        assertThat(responses.get(0))
                .extracting("id", "email", "nickname")
                .contains(memberRequiredOnly.getId(), "email@naver.com", "닉네임");
        assertThat(responses.get(1))
                .extracting("id", "email", "nickname",
                        "profile.imageUrl", "profile.introduction")
                .contains(memberWithProfile.getId(), "email@naver.com", "닉네임", "image URL", "자기소개 글");
        assertThat(responses.get(2))
                .extracting("id", "email", "nickname",
                        "profile.imageUrl", "profile.introduction",
                        "preferTown.latitude", "preferTown.longitude", "preferTown.address", "preferTown.roadAddress")
                .contains(memberWithProfileAndPreferTown.getId(), "email@naver.com", "닉네임", "image URL", "자기소개 글", 48.8, 11.34, "독일 뮌헨", "독일 뮌헨로");
        assertThat(responses.get(3))
                .extracting("id", "email", "nickname", "profile.introduction")
                .contains(memberWithIntro.getId(), "email@naver.com", "닉네임", "자기소개 글");
    }

    @Test
    @DisplayName("예외: 회원 정보 조회 시 회원 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void getNotExistMember() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        member1.delete();
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        Long deletedMemberId = memberRepository.save(member1).getId();
        Long notJoinedMemberId = memberRepository.save(member2).getId() + 1;

        // when // then
        assertThatThrownBy(() -> memberService.getMember(deletedMemberId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
        assertThatThrownBy(() -> memberService.getMember(notJoinedMemberId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("성공: 회원은 자신의 닉네임을 수정할 수 있다.")
    void updateMemberNickname() {
        // given
        Member member = createMember("email@naver.com", "13password%^&", "nickname");
        Long memberId = memberRepository.save(member).getId();

        MemberNicknameRequest request = createMemberNicknameRequest(memberId, "new_nickname");

        // when
        MemberSimpleResponse response = memberService.updateMemberNickname(request);

        // then
        assertThat(response)
                .extracting("id", "nickname")
                .contains(memberId, "new_nickname");
    }

    @Test
    @DisplayName("예외: 넥네임 수정 시 회원 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void updateNotExistMemberNickname() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        member1.delete();
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        Long deletedMemberId = memberRepository.save(member1).getId();
        Long notJoinedMemberId = memberRepository.save(member2).getId() + 1;
        MemberNicknameRequest requestWithDeletedMember = createMemberNicknameRequest(deletedMemberId, "new_nickname1");
        MemberNicknameRequest requestWithNotJoinedMember = createMemberNicknameRequest(notJoinedMemberId, "new_nickname2");

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberNickname(requestWithDeletedMember))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
        assertThatThrownBy(() -> memberService.updateMemberNickname(requestWithNotJoinedMember))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("예외: 닉네임 수정 시 다른 사람이 사용 중이거나 현재와 동일한 닉네임으로 변경 할 경우 예외가 발생한다.")
    void updateMemberNicknameWithUnchangedAndDuplicate() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        MemberNicknameRequest requestWithUnchangedNickname = createMemberNicknameRequest(savedMember1.getId(), savedMember1.getNickname());
        MemberNicknameRequest requestWithDuplicateNickname = createMemberNicknameRequest(savedMember1.getId(), savedMember2.getNickname());

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberNickname(requestWithUnchangedNickname))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 사용 중인 닉네임 입니다.");
        assertThatThrownBy(() -> memberService.updateMemberNickname(requestWithDuplicateNickname))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 사용 중인 닉네임 입니다.");
    }

    @Test
    @DisplayName("성공: 회원은 자신의 비밀번호를 수정할 수 있다.")
    void updateMemberPassword() {
        // given
        Member member = createMember("email@naver.com", "13password%^&", "nickname");
        Long memberId = memberRepository.save(member).getId();

        MemberPasswordRequest request = createMemberPasswordRequest(memberId, "13password%^&", "new13password%^&");

        // when
        memberService.updateMemberPassword(request);

        // then
        Member updatedMember = memberRepository.findById(memberId).orElse(member);
        assertThat(updatedMember)
                .extracting("id", "password")
                .contains(updatedMember.getId(), "new13password%^&");
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 회원 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void updateNotExistMemberPassword() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        member1.delete();
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        Long deletedMemberId = memberRepository.save(member1).getId();
        Long notJoinedMemberId = memberRepository.save(member2).getId() + 1;
        MemberPasswordRequest requestWithDeletedMember = createMemberPasswordRequest(deletedMemberId, "password01%^&", "new13password%^&1");
        MemberPasswordRequest requestWithNotJoinedMember = createMemberPasswordRequest(notJoinedMemberId, "password02%^&", "new13password%^&2");

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberPassword(requestWithDeletedMember))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
        assertThatThrownBy(() -> memberService.updateMemberPassword(requestWithNotJoinedMember))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 현재와 동일한 비밀번호로 변경 할 경우 예외가 발생한다.")
    void updateMemberPasswordWithUnchanged() {
        // given
        Member member = createMember("email@naver.com", "13password%^&", "nickname");
        Member savedMember = memberRepository.save(member);

        MemberPasswordRequest requestWithUnchangedPassword = createMemberPasswordRequest(savedMember.getId(), savedMember.getPassword(), savedMember.getPassword());

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberPassword(requestWithUnchangedPassword))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("현재와 동일한 비밀번호 입니다.");
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 현재 비밀번호가 일치하지 않으면, 현재 비밀번호를 모르면, 비밀번호 수정 자격이 없다. 예외가 발생한다.")
    void updateMemberPasswordWithIncorrectPassword() {
        // given
        Member member = createMember("email@naver.com", "13password%^&", "nickname");
        Member savedMember = memberRepository.save(member);

        MemberPasswordRequest requestWithIncorrectPassword = createMemberPasswordRequest(savedMember.getId(), "incorrect13password%", "new13password%^&");

        // when // then
        assertThatThrownBy(() -> memberService.updateMemberPassword(requestWithIncorrectPassword))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("기존에 등록된 비밀번호와 일치하지 않습니다.");
    }

    @Test
    @DisplayName("성공: 서비스 이용을 종료하기 위해 회원 탈퇴를 할 수 있다.")
    void deleteMember() {
        // given
        Member member = createMember("email@naver.com", "13password%^&", "nickname");
        Long memberId = memberRepository.save(member).getId();

        // when
        memberService.deleteMember(memberId);

        // then
        Member updatedMember = memberRepository.findById(memberId).orElse(member);
        assertThat(updatedMember)
                .extracting("id", "status")
                .contains(updatedMember.getId(), INACTIVE);
    }

    @Test
    @DisplayName("예외: 회원 탈퇴 시 이미 탈퇴 처리 되었거나 존재하지 않는 회원이면 예외가 발생한다.")
    void deleteNotExistMember() {
        // given
        Member member1 = createMember("email01@naver.com", "password01%^&", "nickname01");
        member1.delete();
        Member member2 = createMember("email02@naver.com", "password02%^&", "nickname02");
        Long deletedMemberId = memberRepository.save(member1).getId();
        Long notJoinedMemberId = memberRepository.save(member2).getId() + 1;

        // when // then
        assertThatThrownBy(() -> memberService.deleteMember(deletedMemberId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("존재하지 않는 회원입니다.");
        assertThatThrownBy(() -> memberService.deleteMember(notJoinedMemberId))
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

    private MemberCreateRequest createMemberCreateRequest(String email, String password, String nickname) {
        return MemberCreateRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private MemberNicknameRequest createMemberNicknameRequest(Long id, String nickname) {
        return MemberNicknameRequest.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }

    private MemberPasswordRequest createMemberPasswordRequest(Long id, String password, String newPassword) {
        return MemberPasswordRequest.builder()
                .id(id)
                .password(password)
                .newPassword(newPassword)
                .build();
    }

}