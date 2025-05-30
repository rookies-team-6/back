package com.boanni_back.project.exception.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomError {
    private String code;
    private String message;
    private List<ErrorDetail> details;
    private String path;
    private String method;
}