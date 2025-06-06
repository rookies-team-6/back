package com.boanni_back.project.ai.controller.dto;

import com.boanni_back.project.ai.entity.GlobalSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class GlobalSummaryDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long question_id;
        private String title;
        private String question;
        private String summary;

        public static Response fromEntity(GlobalSummary summary) {
            return Response.builder()
                    .id(summary.getId())
                    .question_id(summary.getQuestion().getId())
                    .title(summary.getTitle())
                    .summary(summary.getSummary())
                    .question(summary.getQuestion().getQuestion())
                    .build();
        }
    }
}
