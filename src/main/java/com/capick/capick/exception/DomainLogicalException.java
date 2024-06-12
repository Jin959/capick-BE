package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class DomainLogicalException extends BaseException {

    private DomainLogicalException(ApiResponseStatus status) {
        super(status);
    }

    public static DomainLogicalException of(ApiResponseStatus status) {
        return new DomainLogicalException(status);
    }

}
