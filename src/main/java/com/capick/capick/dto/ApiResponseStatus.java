package com.capick.capick.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiResponseStatus {
    // 200
    SUCCESS(HttpStatus.OK, "요청에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "리소스 생성에 성공했습니다."),
    DELETED(HttpStatus.NO_CONTENT, "리소스 삭제에 성공했습니다."),

    // 400
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 계정의 이메일 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임 입니다."),
    NOT_CHANGED_PASSWORD(HttpStatus.CONFLICT, "현재와 동일한 비밀번호 입니다."),

    // 500
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결 또는 접근에 실패하였습니다. 관리자에게 문의해 주세요."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 예상치 못한 오류가 발생 했습니다. 관리자에게 문의해 주세요.");

    private final HttpStatus status;
    private final String message;

}
