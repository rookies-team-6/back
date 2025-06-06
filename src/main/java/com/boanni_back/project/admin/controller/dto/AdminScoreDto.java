package com.boanni_back.project.admin.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminScoreDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String username;
        private String departmentCode;
        private int score;

        public static Response fromEntity(Users user) {
            return Response.builder()
                    .userId(user.getId())
                    .username(user.getEmployeeNumber().getUsername())
                    .departmentCode(user.getEmployeeNumber().getDepartmentCode())
                    .score(user.getScore())
                    .build();
        }
    }
}
