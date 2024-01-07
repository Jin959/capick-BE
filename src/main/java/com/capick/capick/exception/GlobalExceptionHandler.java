package com.capick.capick.exception;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.ApiResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.capick.capick.dto.ApiResponseStatus.DATABASE_ERROR;
import static com.capick.capick.dto.ApiResponseStatus.UNEXPECTED_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<ApiResponseStatus> BindExceptionHandler(BindException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Exception Message : {}", message);
        log.warn("BindException : ", exception);
        return ApiResponse.of(HttpStatus.BAD_REQUEST, message);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<ApiResponseStatus> UnexpectedExceptionHandler(Exception exception) {
        log.warn("Unexpected Exception Message : {}", exception.getMessage());
        log.warn("Exception : ", exception);
        return ApiResponse.of(UNEXPECTED_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public ApiResponse<ApiResponseStatus> DataAccessExceptionHandler(DataAccessException exception) {
        log.warn("Unexpected Exception Message : {}", exception.getMessage());
        log.warn("Exception : ", exception);
        return ApiResponse.of(DATABASE_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public ApiResponse<ApiResponseStatus> BaseExceptionHandler(BaseException exception) {
        log.warn("Exception Message : {}", exception.getMessage());
        log.warn("BaseException : ", exception);
        return ApiResponse.of(exception.getStatus());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateResourceException.class)
    public ApiResponse<ApiResponseStatus> DuplicateResourceExceptionHandler(DuplicateResourceException exception) {
        log.warn("Exception Message : {}", exception.getMessage());
        log.warn("DuplicateResourceException : ", exception);
        return ApiResponse.of(exception.getStatus());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundResourceException.class)
    public ApiResponse<ApiResponseStatus> NotFoundResourceExceptionHandler(NotFoundResourceException exception) {
        log.warn("Exception Message : {}", exception.getMessage());
        log.warn("NotFoundResourceException : ", exception);
        return ApiResponse.of(exception.getStatus());
    }

}
