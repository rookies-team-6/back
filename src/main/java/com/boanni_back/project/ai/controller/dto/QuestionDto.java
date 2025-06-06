package com.boanni_back.project.ai.controller.dto;

import com.boanni_back.project.ai.entity.Question;
import lombok.*;

public class QuestionDto {
    @Data
    public static class Request {
        private String question;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class Response {
        private Long id;
        private String question;
        private boolean canSolve;

        // 정적 팩토리 메서드
        public static Response fromEntity(Question question) {
            return Response.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .build();
        }

        public static Response fromEntity(Question question, boolean canSolve) {
            return Response.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .canSolve(canSolve)
                    .build();
        }
    }
}