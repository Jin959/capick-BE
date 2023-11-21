package com.capick.capick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiResponseStatus {
    // 200
    SUCCESS(HttpStatus.OK, "요청에 성공했습니다."),

    // 400
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // 500
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;

}
