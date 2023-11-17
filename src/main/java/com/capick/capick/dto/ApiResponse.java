package com.capick.capick.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private int code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> of(ApiResponseStatus status, T data) {
        return ApiResponse.<T>builder()
                .code(status.getStatus().value())
                .message(status.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> of(ApiResponseStatus status) {
        return ApiResponse.<T>builder()
                .code(status.getStatus().value())
                .message(status.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(ApiResponseStatus.SUCCESS, data);
    }

}
