package com.capick.capick.service;

import com.capick.capick.domain.member.Member;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import com.capick.capick.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberCreateResponse createMember(MemberCreateRequest request) {
        Member member = memberRepository.save(request.toEntity());
        return MemberCreateResponse.of(member);
    }

}
