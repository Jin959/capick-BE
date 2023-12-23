package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.ApiResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ApiResponse<ApiResponseStatus> BaseExceptionHandler(BaseException exception) {
        log.warn("Exception Message : {}", exception.getMessage());
        log.warn("BaseException : ", exception);
        return ApiResponse.of(exception.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<ApiResponseStatus> BindExceptionHandler(BindException exception) {
        log.warn("Exception Message : {}", exception.getMessage());
        log.warn("BindException : ", exception);
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }

}
