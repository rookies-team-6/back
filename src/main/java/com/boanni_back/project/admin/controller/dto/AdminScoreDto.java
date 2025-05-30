package com.boanni_back.project.admin.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminScoreDto {

    private Long id;
    private String username;
    private int score;

    public static AdminScoreDto fromEntity(Users user) {
        return AdminScoreDto.builder()
                .id(user.getId())
                .username(user.getEmployeeNumber().getUsername())
                .score(user.getScore())
                .build();
    }
}

