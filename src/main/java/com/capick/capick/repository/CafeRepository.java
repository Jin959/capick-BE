package com.capick.capick.repository;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {

    Optional<Cafe> findByKakaoPlaceIdAndStatus(String kakaoPlaceId, BaseStatus status);

}
