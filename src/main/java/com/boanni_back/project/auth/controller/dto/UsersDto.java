package com.boanni_back.project.auth.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UsersDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String email;
        private String password;
        private String employeeType;
        private String username;
        private Long currentQuestionIndex;
        private int score;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String email;
        private String employeeType;
        private String employeeNumber;
        private int score;
        private Long currentQuestionIndex;
        private String username;

        // Entity → Response DTO
        public static Response fromEntity(Users user) {
            return Response.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .employeeType(user.getEmployeeType().name())
                    .employeeNumber(user.getEmployeeNumber().getEmployeeNum())
                    .score(user.getScore())
                    .currentQuestionIndex(user.getCurrentQuestionIndex())
                    .username(user.getEmployeeNumber().getUsername())
                    .build();
        }

        // JSON → Response DTO
        public static Response fromJson(JsonNode json) {
            return Response.builder()
                    .id(json.get("id").asLong())
                    .email(json.get("email").asText())
                    .employeeType(json.get("employeeType").asText())
                    .employeeNumber(json.get("employeeNum").asText())
                    .score(json.get("score").asInt())
                    .currentQuestionIndex(json.get("currentQuestionIndex").asLong())
                    .username(json.get("username").asText())
                    .build();
        }
    }
}
