package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberUpdateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.*;
import static com.capick.capick.dto.ApiResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberCreateResponse createMember(MemberCreateRequest request) {
        ifExistsByEmailThrow(request.getEmail());
        ifExistsByNicknameThrow(request.getNickname());

        Member member = memberRepository.save(request.toEntity());
        return MemberCreateResponse.of(member);
    }

    public MemberResponse getMember(Long memberId) {
        Member member = FindMemberOrElseThrow(memberId);
        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse updateMemberInfo(MemberUpdateRequest memberUpdateRequest) {
        Member member = FindMemberOrElseThrow(memberUpdateRequest.getId());

        String nickname = memberUpdateRequest.getNickname();
        Optional.ofNullable(nickname).ifPresent(this::ifExistsByNicknameThrow);

        member.updateInfo(memberUpdateRequest.getPassword(), nickname);
        Member savedMember = memberRepository.save(member);
        return MemberResponse.of(savedMember);
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

    private Member FindMemberOrElseThrow(Long id) {
        return memberRepository.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_MEMBER));
    }
}
