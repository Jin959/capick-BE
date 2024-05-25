package com.capick.capick.dto.response;

import com.capick.capick.domain.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private Long id;

    private MemberSimpleResponse writer;

    private String visitPurpose;

    private String content;

    private String menu;

    private LocalDateTime registeredAt;

    @Builder
    private ReviewResponse(Long id, MemberSimpleResponse writer, String visitPurpose,
                           String content, String menu, LocalDateTime registeredAt) {
        this.id = id;
        this.writer = writer;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.registeredAt = registeredAt;
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .writer(
                        MemberSimpleResponse.of(review.getWriter())
                )
                .visitPurpose(review.getVisitPurpose())
                .content(review.getContent())
                .menu(review.getMenu())
                .registeredAt(review.getRegisteredAt())
                .build();
    }

}
