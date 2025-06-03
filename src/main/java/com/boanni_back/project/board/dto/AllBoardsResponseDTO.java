package com.boanni_back.project.board.dto;

import com.boanni_back.project.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AllBoardsResponseDTO {
    private Long id;
    private String title;
    private LocalDateTime createdAt;

    public AllBoardsResponseDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.createdAt = board.getCreatedAt();
    }
}
