package com.capick.capick.dto.response;

import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import com.capick.capick.domain.review.ReviewImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewSimpleResponse {

    private Long id;

    private MemberSimpleResponse writer;

    private String visitPurpose;

    private String content;

    private String menu;

    private LocalDateTime registeredAt;

    private List<String> imageUrls;

    @Builder
    private ReviewSimpleResponse(
            Long id, MemberSimpleResponse writer, String visitPurpose,
            String content, String menu, LocalDateTime registeredAt, List<String> imageUrls) {
        this.id = id;
        this.writer = writer;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.registeredAt = registeredAt;
        this.imageUrls = imageUrls;
    }

    public static ReviewSimpleResponse of(Review review, List<ReviewImage> reviewImages, Member writer) {
        return ReviewSimpleResponse.builder()
                .id(review.getId())
                .writer(
                        MemberSimpleResponse.of(writer)
                )
                .visitPurpose(review.getVisitPurpose())
                .content(review.getContent())
                .menu(review.getMenu())
                .registeredAt(review.getRegisteredAt())
                .imageUrls(
                        reviewImages.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
                )
                .build();
    }

}
