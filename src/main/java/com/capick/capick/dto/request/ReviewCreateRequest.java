package com.capick.capick.dto.request;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private String theme;

    @Builder
    public ReviewCreateRequest(Long writerId, CafeCreateRequest cafe, String visitPurpose, String content, String menu,
                               int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme) {
        this.writerId = writerId;
        this.cafe = cafe;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.coffeeIndex = coffeeIndex;
        this.spaceIndex = spaceIndex;
        this.priceIndex = priceIndex;
        this.noiseIndex = noiseIndex;
        this.theme = theme;
    }

    // TODO: 테스트를 위해 리뷰 등록 시간 registeredAt 을 기록하는 로직을 일단 여기에 개발한다. toEntity 는 제거하고 Review.create 으로 개발하고 등록시간 테스트 작성하기
    public Review toEntity(Member writer, Cafe cafe, LocalDateTime registeredAt) {
        return Review.builder()
                .writer(writer)
                .cafe(cafe)
                .visitPurpose(visitPurpose)
                .content(content)
                .menu(menu)
                .theme(theme)
                .registeredAt(registeredAt)
                .build();
    }

}
