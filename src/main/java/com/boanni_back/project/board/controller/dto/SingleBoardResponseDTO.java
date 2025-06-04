package com.boanni_back.project.board.controller.dto;

import com.boanni_back.project.board.entity.Board;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SingleBoardResponseDTO {
    private Long id;

    private String username;

    private String title;

    private String contents;

    private LocalDateTime createdAt;

//    현재 게시글이 로그인한 유저 id와 동일하지 유무 (boolean)
    private boolean isMine;


    public SingleBoardResponseDTO(Board board,Long userId) {
        this.id = board.getId();
        this.username=board.getUsers().getEmployeeNumber().getUsername();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt();
        this.isMine=board.getUsers().getId().equals(userId);
    }
}
