package com.boanni_back.project.admin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode {
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
