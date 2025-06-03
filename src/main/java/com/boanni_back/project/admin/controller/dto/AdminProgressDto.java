package com.boanni_back.project.admin.controller.dto;

import com.boanni_back.project.auth.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminProgressDto {

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
        private Long currentQuestionIndex;
        private String progress;
        private int progressPercent;

        public static Response fromEntity(Users user, long totalQuestions) {
            long index = user.getCurrentQuestionIndex();
            double percent = (index < 0 || totalQuestions == 0) ? 0.0 : (index / (double) totalQuestions) * 100.0;
            int progressPercent = (int) percent;
            String progressStr = String.format("%d%%", progressPercent);

            return Response.builder()
                    .userId(user.getId())
                    .username(user.getEmployeeNumber().getUsername())
                    .departmentCode(user.getEmployeeNumber().getDepartmentCode())
                    .currentQuestionIndex(index)
                    .progress(progressStr)
                    .progressPercent(progressPercent)
                    .build();
        }
    }
}
