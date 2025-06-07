package com.boanni_back.project.board.controller.dto;

import com.boanni_back.project.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AllBoardsResponseDTO {
    private Long id;
    private String title;
    private String createdAt;
    private String role;
    private String author;

    public AllBoardsResponseDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.createdAt = board.getCreatedAt().toString().replace("T", " ");
        this.role = board.getUsers().getEmployeeType().name(); // Enum → String으로 변환
        this.author = board.getUsers().getEmployeeNumber().getUsername();
    }
}
