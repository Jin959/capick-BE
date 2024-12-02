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
public class ReviewDetailResponse {

    private Long id;

    private MemberSimpleResponse writer;

    private String visitPurpose;

    private String content;

    private String menu;

    private Integer coffeeIndex;

    private Integer spaceIndex;

    private Integer priceIndex;

    private Integer noiseIndex;

    private String theme;

    private LocalDateTime registeredAt;

    private List<String> imageUrls;

    @Builder
    private ReviewDetailResponse(
            Long id, MemberSimpleResponse writer, String visitPurpose, String content, String menu,
            Integer coffeeIndex, Integer spaceIndex, Integer priceIndex, Integer noiseIndex,
            String theme, LocalDateTime registeredAt, List<String> imageUrls) {
        this.id = id;
        this.writer = writer;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.coffeeIndex = coffeeIndex;
        this.spaceIndex = spaceIndex;
        this.priceIndex = priceIndex;
        this.noiseIndex = noiseIndex;
        this.theme = theme;
        this.registeredAt = registeredAt;
        this.imageUrls = imageUrls;
    }

    public static ReviewDetailResponse of(Review review, List<ReviewImage> reviewImages, Member writer) {
        return ReviewDetailResponse.builder()
                .id(review.getId())
                .writer(
                        MemberSimpleResponse.of(writer)
                )
                .visitPurpose(review.getVisitPurpose())
                .content(review.getContent())
                .menu(review.getMenu())
                .coffeeIndex(review.getCoffeeIndex())
                .spaceIndex(review.getSpaceIndex())
                .priceIndex(review.getPriceIndex())
                .noiseIndex(review.getNoiseIndex())
                .theme(review.getTheme())
                .registeredAt(review.getRegisteredAt())
                .imageUrls(
                        reviewImages.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
                )
                .build();
    }

}
