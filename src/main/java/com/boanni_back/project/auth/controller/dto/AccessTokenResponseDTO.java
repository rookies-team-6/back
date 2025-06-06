package com.boanni_back.project.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class AccessTokenResponseDTO {
    private String accessToken;
}
