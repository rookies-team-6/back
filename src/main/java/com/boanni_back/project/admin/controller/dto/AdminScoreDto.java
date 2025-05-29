package com.boanni_back.project.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminScoreDto {
    private Long id;
    private String username;
    private int score;
}
