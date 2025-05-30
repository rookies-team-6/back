package com.boanni_back.project.admin.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminProcessDto {

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
        private String progress;

        public static Response fromEntity(Users user, String progress) {
            return Response.builder()
                    .userId(user.getId())
                    .username(user.getEmployeeNumber().getUsername())
                    .progress(progress)
                    .build();
        }
    }
}
