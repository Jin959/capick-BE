package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class UnauthorizedException extends BaseException {

    private UnauthorizedException(ApiResponseStatus status) {
        super(status);
    }

    public static UnauthorizedException of(ApiResponseStatus status) {
        return new UnauthorizedException(status);
    }

}
