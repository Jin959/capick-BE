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
    NO_DATA(HttpStatus.NO_CONTENT, "요청에 성공했습니다."),

    // 400
    INCORRECT_PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, "기존에 등록된 비밀번호와 일치하지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 계정의 이메일 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임 입니다."),
    NOT_CHANGED_PASSWORD(HttpStatus.CONFLICT, "현재와 동일한 비밀번호 입니다."),
    FIRST_REVIEW_WITHOUT_CAFE_LOCATION(HttpStatus.CONFLICT, "까페에 첫 리뷰를 등록할 때는 까페의 위치 정보가 필요합니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "요청을 읽을 수 없습니다. JSON 포맷 및 문법, 필드 타입 등을 확인해 주세요."),
    URI_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "요청에 맞는 API 스펙을 찾을 수 없습니다. HTTP 메서드, URI, URI 의 Path 및 Query 등의 타입과 형식을 확인해 주세요."),
    REVIEW_WITH_CAFE_TYPE_INDEX_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "리뷰 작성 시 까페 타입 지수는 1 부터 5 여야 합니다."),
    NUMBER_OF_REVIEW_IMAGE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지는 최대 3개 까지 등록할 수 있습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    NOT_THE_WRITER(HttpStatus.UNAUTHORIZED, "작성자가 아닙니다."),

    // 500
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결 또는 접근에 실패하였습니다. 관리자에게 문의해 주세요."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 예상치 못한 오류가 발생 했습니다. 관리자에게 문의해 주세요.");

    private final HttpStatus status;
    private final String message;

}
