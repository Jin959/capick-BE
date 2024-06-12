package com.capick.capick.repository;

import com.capick.capick.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
