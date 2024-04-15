package com.capick.capick.dto.request;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    private Long writerId;

    private CafeCreateRequest cafe;

    private String visitPurpose;

    private String content;

    private String menu;

    private int coffeeIndex;

    private int priceIndex;

    private int spaceIndex;

    private int noiseIndex;

    public Review toEntity(Member writer, Cafe cafe) {
        return Review.builder()
                .writer(writer)
                .cafe(cafe)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .coffeeIndex(coffeeIndex)
                .priceIndex(priceIndex)
                .spaceIndex(spaceIndex)
                .noiseIndex(noiseIndex)
                .build();
    }

}
