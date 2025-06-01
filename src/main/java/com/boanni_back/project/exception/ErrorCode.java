package com.boanni_back.project.exception;

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
    AUTH_EMAIL_DUPLICATE_ERROR("A005","중복된 이메일입니다.",HttpStatus.BAD_REQUEST),

    // Question 관련 에러
    INDEX_NOT_FOUND("Q001", "해당 문제를 찾을 수 없습니다. INDEX : %s", HttpStatus.NOT_FOUND),
    NO_MORE_QUESTION("Q002", "더 이상 문제가 없습니다. 마지막 문제 INDEX : %s", HttpStatus.NOT_FOUND),
    NO_QUESTION("Q003", "문제 내용이 비어있습니다. 문제를 입력해주세요.", HttpStatus.NOT_FOUND),

    // record answer 관련 에러
    ANSWER_NOT_FOUND("A001", "해당하는 답변을 찾을 수 없습니다. User ID : %s & Question ID : %s", HttpStatus.NOT_FOUND),
    RECORD_NOT_FOUND("A002", "해당하는 기록을 찾을 수 없습니다. User ID : %s & Question ID : %s", HttpStatus.NOT_FOUND),
    SOLVED_RECORD_NOT_FOUND("A003", "해당 사용자가 푼 문제 기록을 찾을 수 없습니다. User ID : %s", HttpStatus.NOT_FOUND),
    BOOKMARKED_RECORD_NOT_FOUND("A004", "해당 사용자가 북마크한 기록을 찾을 수 없습니다. User ID : %s", HttpStatus.NOT_FOUND),
    // Chat ai 응답 처리
    API_SERVER_ERROR("AI001", "AI 응답 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    API_RESPONSE_TYPE_ERROR("AI002", "응답에 JSON 형식이 포함되어 있지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    //User 학습 기간 관련 에러
    USER_DEADLINE_NOT_FOUND("U010", "입력한 학습 기간이 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    USER_DEADLINE_BEFORE_TODAY("U011", "시작일은 오늘 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    USER_DEADLINE_BEFORE_START("U012", "종료일은 시작일보다 이후여야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
