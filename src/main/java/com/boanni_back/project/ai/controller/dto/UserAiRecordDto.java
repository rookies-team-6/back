package com.boanni_back.project.ai.controller.dto;


import com.boanni_back.project.ai.entity.UserAiRecord;
import lombok.*;

public class UserAiRecordDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Request {
        private Long questionId;
        private Long userId;
        private String userAnswer;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class BookmarkedRequest{
        private Long questionId;
        private Long userId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String userAnswer;
        private String gptAnswer;
        private boolean isBookMarked;
        private QuestionDto.Response question;
        private Long userId;

        public static Response fromEntity(UserAiRecord record) {
            return Response.builder()
                    .id(record.getId())
                    .userAnswer(record.getUserAnswer())
                    .gptAnswer(record.getAiAnswer())
                    .isBookMarked(record.isBookMarked())
                    .question(QuestionDto.Response.fromEntity(record.getQuestion()))
                    .userId(record.getUsers().getId())
                    .build();
        }
    }
}
