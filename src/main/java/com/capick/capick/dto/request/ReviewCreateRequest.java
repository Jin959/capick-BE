package com.capick.capick.dto.request;

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

    // TODO: DTO -> 엔터티 참조 관계 때문에 Member writer, Cafe cafe 파라미터를 여기서 받는게 그나마 나은 것 같다. 개발하기
    public Review toEntity() {
        return Review.builder()
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
