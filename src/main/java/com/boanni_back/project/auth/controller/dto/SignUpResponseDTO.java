package com.boanni_back.project.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpResponseDTO {
    private String employeeNum;
    private String email;
}
