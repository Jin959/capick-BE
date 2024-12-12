package com.capick.capick.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {

    // TODO: 추후 토큰을 이용한 검증으로 변경하고 해당 필드는 제거한다.
    @NotNull(message = "리뷰 작성자의 회원 리소스 아이디를 입력해 주세요.")
    private Long writerId;

    @NotNull(message = "리뷰 수정을 위해 카페를 방문한 목적을 입력해 주세요.")
    @Size(max = 20, message = "카페 방문 목적은 최대 20자입니다.")
    private String visitPurpose;

    @NotNull(message = "리뷰 수정을 위해 리뷰 내용을 입력해 주세요.")
    @Size(max = 300, message = "리뷰 내용은 최대 300자입니다.")
    private String content;

    @NotBlank(message = "리뷰 수정을 위해 리뷰할 메뉴를 입력해 주세요.")
    private String menu;

    @NotNull(message = "리뷰 수정을 위해 커피 맛에 대한 질문에 응답해 주세요.")
    private Integer coffeeIndex;

    @NotNull(message = "리뷰 수정을 위해 공간에 대한 질문에 응답해 주세요.")
    private Integer spaceIndex;

    @NotNull(message = "리뷰 수정을 위해 가격에 대한 질문에 응답해 주세요.")
    private Integer priceIndex;

    @NotNull(message = "리뷰 수정을 위해 소음에 대한 질문에 응답해 주세요.")
    private Integer noiseIndex;

    @NotNull(message = "리뷰 수정을 위해 컨셉이나 테마에 대한 질문에 응답해 주세요.")
    private String theme;

    private List<@Pattern(
            regexp = "((https?)://)?([\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥][\\-_./%]?)+(\\?([^#\\s]+)?)?$",
            message = "리뷰 이미지 중 허용되지 않는 URL 이 존재합니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS 를 사용해 주세요."
    ) String> imageUrls = new ArrayList<>();

    @Builder
    public ReviewUpdateRequest(
            Long writerId, String visitPurpose, String content, String menu, Integer coffeeIndex,
            Integer spaceIndex, Integer priceIndex, Integer noiseIndex, String theme, List<String> imageUrls) {
        this.writerId = writerId;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.coffeeIndex = coffeeIndex;
        this.spaceIndex = spaceIndex;
        this.priceIndex = priceIndex;
        this.noiseIndex = noiseIndex;
        this.theme = theme;
        this.imageUrls = imageUrls;
    }

}
