package com.boanni_back.project.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequestDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
