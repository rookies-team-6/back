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
public class AdminProcessDto {
    private Long userId;
    private String username;
    private String progress;

    public static AdminProcessDto fromEntity(Users user, String progress) {
        return AdminProcessDto.builder()
                .userId(user.getId())
                .username(user.getEmployeeNumber().getUsername())
                .progress(progress)
                .build();
    }
}