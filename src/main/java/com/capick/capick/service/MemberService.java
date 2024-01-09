package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberPasswordRequest;
import com.capick.capick.dto.request.MemberNicknameRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.capick.capick.domain.common.BaseStatus.*;
import static com.capick.capick.dto.ApiResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberSimpleResponse createMember(MemberCreateRequest request) {
        ifExistsByEmailThrow(request.getEmail());
        ifExistsByNicknameThrow(request.getNickname());

        Member member = memberRepository.save(request.toEntity());
        return MemberSimpleResponse.of(member);
    }

    public MemberResponse getMember(Long memberId) {
        Member member = findMemberByIdOrElseThrow(memberId);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberSimpleResponse updateMemberNickname(MemberNicknameRequest memberNicknameRequest) {
        Member member = findMemberByIdOrElseThrow(memberNicknameRequest.getId());

        String nickname = memberNicknameRequest.getNickname();
        ifExistsByNicknameThrow(nickname);

        member.updateNickname(nickname);
        Member savedMember = memberRepository.save(member);
        return MemberSimpleResponse.of(savedMember);
    }

    @Transactional
    public void updateMemberPassword(MemberPasswordRequest memberPasswordRequest) {
        Member member = findMemberByIdOrElseThrow(memberPasswordRequest.getId());
        member.updatePassword(memberPasswordRequest.getPassword());
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = findMemberByIdOrElseThrow(memberId);
        member.delete();
        memberRepository.save(member);
    }

    private void ifExistsByEmailThrow(String email) {
        if (memberRepository.existsByEmailAndStatus(email, ACTIVE)) {
            throw DuplicateResourceException.of(DUPLICATE_EMAIL);
        }
    }

    private void ifExistsByNicknameThrow(String nickname) {
        if (memberRepository.existsByNicknameAndStatus(nickname, ACTIVE)) {
            throw DuplicateResourceException.of(DUPLICATE_NICKNAME);
        }
    }

    private Member findMemberByIdOrElseThrow(Long id) {
        return memberRepository.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_MEMBER));
    }
}
