package com.capick.capick.domain.history.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileDomain {

    MEMBER("회원", "member", "members"),
    REVIEW("리뷰", "review", "reviews"),
    ETC("도메인 미정 및 기타 도메인", "etc", "etcs");

    private final String text;
    private final String domain;
    private final String domainInPlural;

}
