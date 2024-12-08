package com.capick.capick.repository;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndStatus(Long id, BaseStatus status);

    @EntityGraph(attributePaths = {"writer"})
    Optional<Review> findWithMemberByIdAndStatus(Long id, BaseStatus status);

    Page<Review> findPageByCafeAndStatus(Cafe cafe, BaseStatus status, Pageable pageable);

}
