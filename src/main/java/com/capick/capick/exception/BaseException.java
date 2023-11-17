package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class BaseException extends RuntimeException {

    private ApiResponseStatus status;

    public BaseException(ApiResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public static BaseException of(ApiResponseStatus status) {
        return new BaseException(status);
    }

}
