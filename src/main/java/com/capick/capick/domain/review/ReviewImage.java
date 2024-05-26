package com.capick.capick.domain.review;

import com.capick.capick.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
