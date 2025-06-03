package com.boanni_back.project.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WriteBoardRequestDTO {
    private String title;

    private String contents;
}
