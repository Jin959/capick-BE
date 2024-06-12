package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static com.capick.capick.dto.ApiResponseStatus.*;

@Component
@RequiredArgsConstructor
public class MemberServiceHelper {

    private final MemberRepository memberRepository;

    public void ifExistsByEmailThrow(String email) {
        if (memberRepository.existsByEmailAndStatus(email, ACTIVE)) {
            throw DuplicateResourceException.of(DUPLICATE_EMAIL);
        }
    }

    public void ifExistsByNicknameThrow(String nickname) {
        if (memberRepository.existsByNicknameAndStatus(nickname, ACTIVE)) {
            throw DuplicateResourceException.of(DUPLICATE_NICKNAME);
        }
    }

    public Member findMemberByIdOrElseThrow(Long id) {
        return memberRepository.findByIdAndStatus(id, ACTIVE)
                .orElseThrow(() -> NotFoundResourceException.of(NOT_FOUND_MEMBER));
    }

}
