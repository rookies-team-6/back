package com.boanni_back.project.board.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriteBoardRequestDTO {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "본문 내용을 입력해주세요.")
    private String contents;
}
