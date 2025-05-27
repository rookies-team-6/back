package com.boanni_back.project.admin.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdminGlobalExceptionHandler {

    @ExceptionHandler(AdminBusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(AdminBusinessException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        error.put("status", e.getHttpStatus().toString());
        return new ResponseEntity<>(error, e.getHttpStatus());
    }
}
