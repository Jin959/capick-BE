package com.capick.capick.dto.response;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewSimpleResponse {

    private Long id;

    private CafeSimpleResponse cafe;

    private String visitPurpose;

    private String content;

    private String menu;

    private LocalDateTime registeredAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String thumbnailUrl;

    @Builder
    private ReviewSimpleResponse(
            Long id, CafeSimpleResponse cafe, String visitPurpose,
            String content, String menu, LocalDateTime registeredAt, String thumbnailUrl) {
        this.id = id;
        this.cafe = cafe;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.registeredAt = registeredAt;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static ReviewSimpleResponse of(Review review, ReviewImage thumbnail, Cafe cafe) {
        return ReviewSimpleResponse.builder()
                .id(review.getId())
                .cafe(
                        CafeSimpleResponse.of(cafe)
                )
                .visitPurpose(review.getVisitPurpose())
                .content(review.getContent())
                .menu(review.getMenu())
                .registeredAt(review.getRegisteredAt())
                .thumbnailUrl(
                        thumbnail != null ? thumbnail.getImageUrl() : null
                )
                .build();
    }

}
