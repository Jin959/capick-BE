package com.capick.capick.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private Long id;

    private MemberSimpleResponse writer;

    private String visitPurpose;

    private String content;

    private String menu;

    private LocalDateTime createdAt;

}
