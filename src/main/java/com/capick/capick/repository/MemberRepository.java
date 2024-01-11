package com.capick.capick.repository;

import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailAndStatus(String email, BaseStatus status);

    boolean existsByNicknameAndStatus(String nickname, BaseStatus status);

    Optional<Member> findByIdAndStatus(Long id, BaseStatus status);
}
