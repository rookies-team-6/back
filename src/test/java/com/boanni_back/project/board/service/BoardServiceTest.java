package com.boanni_back.project.board.service;

import com.boanni_back.project.board.controller.dto.WriteBoardRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Test
    void inputExampleData(){
        for (int i = 0; i < 100; i++) {
            WriteBoardRequestDTO request = WriteBoardRequestDTO.builder()
                    .title(String.format("%d번째 제목", i))
                    .contents(String.format("%d번째 본문", i))
                    .build();

            this.boardService.writeBoard(request, 1L);
        }
    }
}
