package com.capick.capick.dto.request;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = "리뷰 작성자의 회원 리소스 아이디를 입력해 주세요.")
    private Long writerId;

    @Valid
    @NotNull(message = "리뷰를 작성할 카페의 정보를 입력해 주세요.")
    private CafeCreateRequest cafe;

    @NotNull(message = "리뷰 작성을 위해 카페를 방문한 목적을 입력해 주세요.")
    @Size(max = 20, message = "카페 방문 목적은 최대 20자입니다.")
    private String visitPurpose;

    @NotNull(message = "리뷰 내용을 입력해 주세요.")
    @Size(max = 300, message = "리뷰 내용은 최대 300자입니다.")
    private String content;

    @NotBlank(message = "리뷰할 메뉴를 입력해 주세요.")
    private String menu;

    @NotNull(message = "리뷰하기 위해 커피 맛에 대한 질문에 응답해 주세요.")
    private Integer coffeeIndex;

    @NotNull(message = "리뷰하기 위해 공간에 대한 질문에 응답해 주세요.")
    private Integer spaceIndex;

    @NotNull(message = "리뷰하기 위해 가격에 대한 질문에 응답해 주세요.")
    private Integer priceIndex;

    @NotNull(message = "리뷰하기 위해 소음에 대한 질문에 응답해 주세요.")
    private Integer noiseIndex;

    @NotNull(message = "리뷰하기 위해 컨셉이나 테마에 대한 질문에 응답해 주세요.")
    private String theme;

    private List<@Pattern(
            regexp = "((https?)://)?([\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥][\\-_./%]?)+(\\?([^#\\s]+)?)?$",
            message = "리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."
    ) String> imageUrls = new ArrayList<>();

    @Builder
    public ReviewCreateRequest(
            Long writerId, CafeCreateRequest cafe, String visitPurpose, String content, String menu, Integer coffeeIndex,
            Integer spaceIndex, Integer priceIndex, Integer noiseIndex, String theme, List<String> imageUrls) {
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
        // TODO: 리뷰 생성 서비스 테스트를 위해 다음과 같이 작성함. 더 좋은 방법이 없을지 생각해보기
        this.imageUrls = Optional.ofNullable(imageUrls).orElseGet(ArrayList::new);
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
