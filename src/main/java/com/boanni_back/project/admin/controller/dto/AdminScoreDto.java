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
        private Long userId; //점수 업데이트 요청 시
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String username;
        private int score;

        public static Response fromEntity(Users user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getEmployeeNumber().getUsername())
                    .score(user.getScore())
                    .build();
        }
    }
}
