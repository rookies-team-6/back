package com.boanni_back.project.admin.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AdminBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;
    private HttpStatus httpStatus;

    public AdminBusinessException(String message) {
        //417
        this(message, HttpStatus.EXPECTATION_FAILED);
    }

    public AdminBusinessException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public AdminBusinessException(AdminErrorCode errorCode, Object... args) {
        this.message = errorCode.formatMessage(args);
        this.httpStatus = errorCode.getHttpStatus();
    }
}