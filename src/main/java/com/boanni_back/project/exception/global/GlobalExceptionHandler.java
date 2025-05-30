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


    // BusinessException 발생했을 시
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {

        CustomError error = new CustomError(
                e.getErrorCode().getCode(),
                e.getMessage(),
                null, // 상세 필드 없음
                request.getRequestURI(),
                request.getMethod()
        );

        ErrorResponse response = new ErrorResponse(
                false,
                error,
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
        return new ResponseEntity<>(response, e.getHttpStatus());
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
                request.getRequestURI(),
                request.getMethod()
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
                request.getRequestURI(),
                request.getMethod()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // validation 검증 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        log.error("Unhandled ValidException: ", e);

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

        ErrorResponse response = new ErrorResponse(
                false,
                error,
                LocalDateTime.now(),
                UUID.randomUUID().toString()
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
                request.getRequestURI(),
                request.getMethod()
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
                request.getRequestURI(),
                request.getMethod()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 공통 에러 응답 메서드
    private ErrorResponse createErrorResponse(HttpStatus status, String message, String code, String path, String method) {
        CustomError error = new CustomError(
                code,
                message,
                null,
                path,
                method
        );

        return new ErrorResponse(
                false,
                error,
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
    }

    // 성공 시
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

        if (body instanceof ErrorResponse) {
            return body;  // 에러 응답은 그대로 반환
        }

        if (body instanceof SuccessResponse) {
            return body;  // 이미 래핑된 경우
        }

        // 성공 응답 자동 래핑
        return new SuccessResponse<>(
                true,
                body,
                "요청이 성공적으로 처리되었습니다.",
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
    }


}
