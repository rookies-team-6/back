package com.boanni_back.project.admin.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class AdminDeadlineDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private LocalDate newDeadline; //수정할 마감일
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long userId;
        private String username;
        private String employeeType;
        private String departmentCode;
        private LocalDate oldDeadline;
        private LocalDate newDeadline;

        public static Response fromEntity(Users user, LocalDate oldDeadline) {
            return Response.builder()
                    .userId(user.getId())
                    .username(user.getEmployeeNumber().getUsername())
                    .employeeType(user.getEmployeeType().name())
                    .departmentCode(user.getEmployeeNumber().getDepartmentCode())
                    .oldDeadline(oldDeadline)
                    .newDeadline(user.getQuestionSolveDeadline())
                    .build();
        }
    }
}