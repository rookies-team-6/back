package com.boanni_back.project.exception.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss E a", timezone = "Asia/Seoul")
    private LocalDateTime timestamp;
    private String path;
    private Integer statusCode;
    private String code;
    private String message;
}
