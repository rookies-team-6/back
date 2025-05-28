package com.boanni_back.project.gpt.controller.dto;

import com.boanni_back.project.gpt.entity.Question;
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

        // 정적 팩토리 메서드
        public static Response fromEntity(Question question) {
            return Response.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .build();
        }
    }
}