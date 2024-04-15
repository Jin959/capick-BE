package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberPasswordRequest;
import com.capick.capick.dto.request.MemberNicknameRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberServiceHelper memberServiceHelper;

    @Transactional
    public MemberSimpleResponse createMember(MemberCreateRequest request) {
        memberServiceHelper.ifExistsByEmailThrow(request.getEmail());
        memberServiceHelper.ifExistsByNicknameThrow(request.getNickname());

        Member member = memberRepository.save(request.toEntity());
        return MemberSimpleResponse.of(member);
    }

    public MemberResponse getMember(Long memberId) {
        Member member = memberServiceHelper.findMemberByIdOrElseThrow(memberId);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberSimpleResponse updateMemberNickname(MemberNicknameRequest memberNicknameRequest) {
        Member member = memberServiceHelper.findMemberByIdOrElseThrow(memberNicknameRequest.getId());

        String nickname = memberNicknameRequest.getNickname();
        memberServiceHelper.ifExistsByNicknameThrow(nickname);

        member.updateNickname(nickname);
        Member savedMember = memberRepository.save(member);
        return MemberSimpleResponse.of(savedMember);
    }

    @Transactional
    public void updateMemberPassword(MemberPasswordRequest memberPasswordRequest) {
        Member member = memberServiceHelper.findMemberByIdOrElseThrow(memberPasswordRequest.getId());
        member.updatePassword(memberPasswordRequest.getPassword(), memberPasswordRequest.getNewPassword());
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberServiceHelper.findMemberByIdOrElseThrow(memberId);
        member.delete();
        memberRepository.save(member);
    }

}
