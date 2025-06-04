package com.boanni_back.project.auth.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        private String departmentCode;
        private int score;
        private Long currentQuestionIndex;
        private String username;
        private LocalDateTime createdAt;
        private LocalDate questionSolveDeadline;
        private String progress;
        private int progressPercent; //html의 막대 bar 표현을 위해 % 제거

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
                    .createdAt(user.getCreateAt())
                    .questionSolveDeadline(user.getQuestionSolveDeadline())
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
                    .createdAt(LocalDateTime.parse(json.get("createdAt").asText()))
                    .questionSolveDeadline(LocalDate.parse(json.get("questionSolveDeadline").asText()))
                    .build();
        }

        //학습 진행률 메서드
        public static Response fromEntityWithProgress(Users user, long totalQuestions) {
            double progressRaw = (totalQuestions > 0)
                    ? (user.getCurrentQuestionIndex() / (double) totalQuestions) * 100.0
                    : 0.0;

            String progressStr = String.format("%.0f%%", progressRaw);
            int progressInt = (int) progressRaw;

            return Response.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .employeeType(user.getEmployeeType().name())
                    .employeeNumber(user.getEmployeeNumber().getEmployeeNum())
                    .departmentCode(user.getEmployeeNumber().getDepartmentCode())
                    .score(user.getScore())
                    .currentQuestionIndex(user.getCurrentQuestionIndex())
                    .username(user.getEmployeeNumber().getUsername())
                    .progress(progressStr)
                    .progressPercent(progressInt)
                    .createdAt(user.getCreateAt())
                    .questionSolveDeadline(user.getQuestionSolveDeadline())
                    .build();
        }
    }
}
