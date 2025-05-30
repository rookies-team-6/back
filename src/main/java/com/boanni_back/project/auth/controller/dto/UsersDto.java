package com.boanni_back.project.auth.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersDto {
    private Long id;
    private String email;
    private String employeeType;
    private int score;
    private Long currentQuestionIndex;
    private String username;

    // 엔티티 → DTO
    public static UsersDto fromEntity(Users user) {
        return UsersDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .employeeType(user.getEmployeeType().name())
                .score(user.getScore())
                .currentQuestionIndex(user.getCurrentQuestionIndex())
                .username(user.getEmployeeNumber().getUsername())
                .build();
    }

    // Json → DTO
    public static UsersDto fromJson(JsonNode json) {
        return UsersDto.builder()
                .id(json.get("id").asLong())
                .email(json.get("email").asText())
                .employeeType(json.get("employeeType").asText())
                .score(json.get("score").asInt())
                .currentQuestionIndex(json.get("currentQuestionIndex").asLong())
                .username(json.get("username").asText())
                .build();
    }
}

