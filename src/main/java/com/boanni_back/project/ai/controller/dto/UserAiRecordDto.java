package com.boanni_back.project.ai.controller.dto;


import com.boanni_back.project.ai.entity.UserAiRecord;
import lombok.*;

public class UserAiRecordDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long questionId;
        private Long userId;
        private String userAnswer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String userAnswer;
        private String gptAnswer;
        private boolean isBookMarked;
        private Long questionId;
        private Long userId;

        public static Response fromEntity(UserAiRecord record) {
            return Response.builder()
                    .id(record.getId())
                    .userAnswer(record.getUserAnswer())
                    .gptAnswer(record.getAiAnswer())
                    .isBookMarked(record.isBookMarked())
                    .questionId(record.getQuestion().getId())
                    .userId(record.getUsers().getId())
                    .build();
        }
    }
}
