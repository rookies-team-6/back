package com.boanni_back.project.board.controller.dto;

import com.boanni_back.project.board.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SingleBoardResponseDTO {
    private Long id;
    @NotBlank
    private String username;

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    @NotBlank
    private LocalDateTime createdAt;

    public SingleBoardResponseDTO(Board board) {
        this.id = board.getId();
        this.username=board.getUsers().getEmployeeNumber().getUsername();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt();
    }
}
