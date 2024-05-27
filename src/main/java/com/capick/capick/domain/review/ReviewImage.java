package com.capick.capick.domain.review;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.dto.ApiResponseStatus.NUMBER_OF_REVIEW_IMAGE_EXCEEDED;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Builder
    private ReviewImage(String imageUrl, Review review) {
        this.imageUrl = imageUrl;
        this.review = review;
    }

    public static ReviewImage create(String imageUrl, Review review) {
        return ReviewImage.builder()
                .imageUrl(imageUrl)
                .review(review)
                .build();
    }

    public static List<ReviewImage> createReviewImages(List<String> imageUrls, Review review) {
        if (imageUrls.size() > 3) {
            throw DomainPoliticalArgumentException.of(NUMBER_OF_REVIEW_IMAGE_EXCEEDED);
        }
        return imageUrls.stream()
                .map(imageUrl -> ReviewImage.create(imageUrl, review)).collect(Collectors.toList());
    }

}
