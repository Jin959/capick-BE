package com.capick.capick.repository;

import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReviewAndStatus(Review review, BaseStatus status);

    @Query("select min(ri) from ReviewImage ri join ri.review r where r in :reviews and ri.status = :status group by r")
    List<ReviewImage> findReviewThumbnailsBy(
            @Param("reviews") List<Review> reviews, @Param("status") BaseStatus status);

}
