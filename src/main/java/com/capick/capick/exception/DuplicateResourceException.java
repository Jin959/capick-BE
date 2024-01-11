package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class DuplicateResourceException extends BaseException {

    private DuplicateResourceException(ApiResponseStatus status) {
        super(status);
    }

    public static DuplicateResourceException of(ApiResponseStatus status) {
        return new DuplicateResourceException(status);
    }

}
