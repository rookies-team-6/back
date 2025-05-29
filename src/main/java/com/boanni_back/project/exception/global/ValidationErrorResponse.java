package com.boanni_back.project.exception.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ValidationErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss E a", timezone = "Asia/Seoul")
    private LocalDateTime timestamp;

    private int statusCode;  // status → statusCode로 변경
    private String message;
    private Map<String, String> errors;

}