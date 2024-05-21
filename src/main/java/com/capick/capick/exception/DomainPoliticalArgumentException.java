package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponseStatus;

public class DomainPoliticalArgumentException extends BaseException {

    private DomainPoliticalArgumentException(ApiResponseStatus status) {
        super(status);
    }

    public static DomainPoliticalArgumentException of(ApiResponseStatus status) {
        return new DomainPoliticalArgumentException(status);
    }

}
