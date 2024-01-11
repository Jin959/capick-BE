package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class NotFoundResourceException extends BaseException {
    private NotFoundResourceException(ApiResponseStatus status) {
        super(status);
    }

    public static NotFoundResourceException of(ApiResponseStatus status) {
        return new NotFoundResourceException(status);
    }
}
