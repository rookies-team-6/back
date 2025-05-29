package com.boanni_back.project.exception;

import com.boanni_back.project.auth.entity.EmployeeAuth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    EMPLOYEE_AUTH_ERROR("EA001","존재하지 않는 사번이거나 가입 완료된 사번입니다.",HttpStatus.NOT_FOUND),
//    AUTH 관련 에러입니다.
//    AUTH_EMAIL_FORMAT_ERROR("A001","형식에 맞지 않는 이메일입니다.",HttpStatus.BAD_REQUEST),
    AUTH_PASSWORD_NOT_EQUAL_ERROR("A002","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_USER_DUPLICATE_ERROR("A003","중복된 유저 이름입니다.",HttpStatus.BAD_REQUEST),
    AUTH_NOT_INCLUDED_EMPLOYEE_NUMBER_ERROR("A004","존재하지 않는 사원 타입입니다.",HttpStatus.BAD_REQUEST),
    AUTH_EMAIL_DUPLICATE_ERROR("A005","중복된 이메일입니다.",HttpStatus.BAD_REQUEST);
    INDEX_NOT_FOUND("U002", "해당 문제를 찾을 수 없습니다. INDEX : %s", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
