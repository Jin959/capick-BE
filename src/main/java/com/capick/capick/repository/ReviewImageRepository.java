package com.capick.capick.repository;

import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReviewAndStatus(Review review, BaseStatus status);

}
