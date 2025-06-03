package com.boanni_back.project.board.controller;

import com.boanni_back.project.auth.entity.CustomUserDetails;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.board.dto.AllBoardsResponseDTO;
import com.boanni_back.project.board.dto.SingleBoardResponseDTO;
import com.boanni_back.project.board.dto.WriteBoardRequestDTO;
import com.boanni_back.project.board.entity.Board;
import com.boanni_back.project.board.repository.BoardRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private BoardRepository boardRepository;
    private UsersRepository usersRepository;

    @GetMapping
    public ResponseEntity<List<AllBoardsResponseDTO>> getAllBoards(){
        List<Board> boards = boardRepository.findAll();
        List<AllBoardsResponseDTO> response = boards.stream()
                .map(AllBoardsResponseDTO::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleBoardResponseDTO> getSingleBoard(@PathVariable Long id){
        Board board = boardRepository.findByIdWithUser(id).orElseThrow(()->new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));
        SingleBoardResponseDTO response=new SingleBoardResponseDTO(board);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<Void> writeBoard(@RequestBody WriteBoardRequestDTO request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_NOT_FOUND_BY_ID));

        Board board = Board.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .users(user)
                .build();

        boardRepository.save(board);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoard(@PathVariable Long id,
                                            @RequestBody WriteBoardRequestDTO request,
                                            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Board board = boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));

        // 소유자 확인
        if (!board.getUsers().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN_USER);
        }

        board.setTitle(request.getTitle());
        board.setContents(request.getContents());
        boardRepository.save(board);

        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Board board = boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));

        // 소유자 확인
        if (!board.getUsers().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN_USER);
        }

        boardRepository.delete(board);
        return ResponseEntity.noContent().build();
    }


}
