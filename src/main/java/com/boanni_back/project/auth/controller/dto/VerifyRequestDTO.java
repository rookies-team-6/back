package com.boanni_back.project.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRequestDTO {
    @NotBlank(message = "사용자 이름을 입력하세요.")
    private String username;

    @NotBlank(message = "사원 번호를 입력하세요.")
    private String employeeNum;
}
