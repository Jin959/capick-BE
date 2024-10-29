package com.capick.capick.domain.history.storage;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.capick.capick.dto.ApiResponseStatus.ILLEGAL_FILE_DOMAIN_ERROR;

@Getter
@RequiredArgsConstructor
public enum FileDomain {

    MEMBER("회원", "member", "members"),
    REVIEW("리뷰", "review", "reviews"),
    ETC("도메인 미정 및 기타 도메인", "etc", "etcs");

    private final String text;
    private final String domain;
    private final String domainInPlural;

    public static FileDomain findByDomainOrDomainInPlural(String domain) {
        return Arrays.stream(FileDomain.values())
                .filter(
                        fileDomain -> fileDomain.getDomain().equals(domain)
                                || fileDomain.getDomainInPlural().equals(domain)
                )
                .findFirst()
                .orElseThrow(() -> DomainPoliticalArgumentException.of(ILLEGAL_FILE_DOMAIN_ERROR));
    }
}
