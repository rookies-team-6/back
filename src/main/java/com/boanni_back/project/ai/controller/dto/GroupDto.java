package com.boanni_back.project.ai.controller.dto;

import com.boanni_back.project.ai.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GroupDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long id;
        private String title;
        private String summary;
        private Long questionId;       // Question 엔티티 대신 ID만 사용
        private String departmentCode;

        public static GroupDto.Response fromEntity(Group group) {
            return GroupDto.Response.builder()
                    .id(group.getId())
                    .title(group.getTitle())
                    .summary(group.getSummary())
                    .questionId(group.getQuestion().getId())
                    .departmentCode(group.getDepartmentCode())
                    .build();
        }
    }
}
