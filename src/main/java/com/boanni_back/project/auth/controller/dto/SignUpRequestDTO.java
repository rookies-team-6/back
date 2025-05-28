package com.boanni_back.project.auth.controller.dto;

import com.boanni_back.project.auth.entity.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    @NotBlank(message="사용자 이름을 입력해주세요.")
    private String username;

    @NotBlank(message="이메일을 입력해주세요.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "올바른 이메일 형식을 입력해주세요. 예: user@example.com"
    )    private String email;

    @NotBlank(message="비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message="비밀번호 확인을 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message="사원 타입을 입력해주세요.")
    private EmployeeType employeeType;
}
