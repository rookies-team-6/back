package com.boanni_back.project.exception.global;

import com.boanni_back.project.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // BusinessException 발생했을 시
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatusCode(e.getHttpStatus().value());
        response.setMessage(e.getMessage());
        response.setCode(e.getErrorCode().getCode());
        response.setPath(request.getRequestURI());

        log.error("Unhandled BusinessException: ", e);
        return new ResponseEntity<ErrorResponse>(response, e.getHttpStatus());
    }

    // 400, JSON 요청 형식이 틀릴 시
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.error("Unhandled HttpMessageNotReadableException: ", e);
        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "잘못된 요청 형식입니다. JSON 문법을 확인해주세요.",
                "INVALID_REQUEST_FORMAT",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 500에러 시
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {

        log.error("Unhandled RuntimeException: ", e);
        ErrorResponse response = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 오류가 발생했습니다. 다시 시도해주세요.",
                "INTERNAL_SERVER_ERROR",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // validation 검증 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Unhandled ValidException: ", e);

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((error) -> {
                errors.put(error.getField(), error.getDefaultMessage());
        });

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(), //400
                        "Validation Failed",
                        errors
                );

        return ResponseEntity.badRequest().body(response);
    }

    // 400, 파라미터 누락 시
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e, HttpServletRequest request) {

        log.error("Missing request parameter: ", e);
        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName(),
                "MISSING_REQUEST_PARAM",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 400, 파라미터 타입 불일치 시
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        log.error("Parameter type mismatch: ", e);
        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "알 수 없음";
        String message = String.format("요청 파라미터 '%s'는 %s 타입이어야 합니다.", paramName, requiredType);

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                "INVALID_PARAM_TYPE",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 공통 에러 응답 메서드
    private ErrorResponse createErrorResponse(HttpStatus status, String message, String code, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatusCode(status.value());
        errorResponse.setMessage(message);
        errorResponse.setCode(code);
        errorResponse.setPath(path);
        return errorResponse;
    }
}
