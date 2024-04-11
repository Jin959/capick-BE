package com.capick.capick.dto.request;

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

}
