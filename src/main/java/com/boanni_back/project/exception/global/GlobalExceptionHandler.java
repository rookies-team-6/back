package com.boanni_back.project.exception.global;

import com.boanni_back.project.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {

    private final HttpServletRequest request;

    // ========================== 비즈니스 예외 ==========================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        CustomError error = new CustomError(
                e.getErrorCode().getCode(),
                e.getMessage(),
                null,
                request.getRequestURI(),
                request.getMethod()
        );

        ErrorResponse response = buildErrorResponse(false, e.getHttpStatus().value(), error);
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    // ========================== JSON 파싱 에러 ==========================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpMessageNotReadableException e) {
        log.error("Unhandled HttpMessageNotReadableException: ", e);
        return buildCommonErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다. JSON 문법을 확인해주세요.", "INVALID_REQUEST_FORMAT");
    }

    // ========================== 서버 에러 ==========================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Unhandled RuntimeException: ", e);
        return buildCommonErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 다시 시도해주세요.", "INTERNAL_SERVER_ERROR");
    }

    // ========================== 유효성 검증 에러 ==========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("Validation Failed: ", e);

        List<ErrorDetail> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .toList();

        CustomError error = new CustomError(
                "VALIDATION_ERROR",
                "입력값이 올바르지 않습니다",
                details,
                request.getRequestURI(),
                request.getMethod()
        );

        ErrorResponse response = buildErrorResponse(false, HttpStatus.BAD_REQUEST.value(), error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        log.error("BadCredentialsException: ", e);
        CustomError error = new CustomError(
                "INVALID_CREDENTIALS",
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                null,
                request.getRequestURI(),
                request.getMethod()
        );

        ErrorResponse response = buildErrorResponse(false, HttpStatus.UNAUTHORIZED.value(), error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ========================== 파라미터 누락 ==========================
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.error("Missing request parameter: ", e);
        return buildCommonErrorResponse(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName(), "MISSING_REQUEST_PARAM");
    }

    // ========================== 파라미터 타입 불일치 ==========================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("Parameter type mismatch: ", e);

        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "알 수 없음";
        String message = String.format("요청 파라미터 '%s'는 %s 타입이어야 합니다.", paramName, requiredType);

        return buildCommonErrorResponse(HttpStatus.BAD_REQUEST, message, "INVALID_PARAM_TYPE");
    }

    // ========================== 공통 에러 생성 메서드 ==========================
    private ResponseEntity<ErrorResponse> buildCommonErrorResponse(HttpStatus status, String message, String code) {
        CustomError error = new CustomError(
                code,
                message,
                null,
                request.getRequestURI(),
                request.getMethod()
        );

        ErrorResponse response = buildErrorResponse(false, status.value(), error);
        return new ResponseEntity<>(response, status);
    }

    private ErrorResponse buildErrorResponse(boolean success, int code, CustomError error) {
        return new ErrorResponse(
                success,
                code,
                error,
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
    }

    // ========================== 성공 응답 자동 래핑 ==========================
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(ErrorResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest serverHttpRequest,
                                  @NonNull ServerHttpResponse serverHttpResponse) {

        if (body instanceof ErrorResponse) return body;
        if (body instanceof SuccessResponse) return body;

        // 기본적으로 HTTP 200으로 성공 응답 래핑
        return new SuccessResponse<>(
                true,
                200,
                body,
                "요청이 성공적으로 처리되었습니다.",
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
    }
}
