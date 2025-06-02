package com.boanni_back.project.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponseDTO {
    private String token;
    private String tokenType;
}
