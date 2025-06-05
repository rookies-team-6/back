package com.boanni_back.project.ai.controller.dto;

import com.boanni_back.project.ai.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class GroupAverageScoreDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long groupNum;
        private int averageScore;

        public static Response fromEntity(Group group, int averageScore) {
            return Response.builder()
                    .groupNum(group.getGroupNum())
                    .averageScore(averageScore)
                    .build();
        }
    }
}

