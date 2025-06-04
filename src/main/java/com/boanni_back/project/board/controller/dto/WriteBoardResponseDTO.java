package com.boanni_back.project.board.controller.dto;

import lombok.Data;

@Data
public class WriteBoardResponseDTO {
    private Long boardId;

    public WriteBoardResponseDTO(Long boardId) {
        this.boardId = boardId;
    }
}
