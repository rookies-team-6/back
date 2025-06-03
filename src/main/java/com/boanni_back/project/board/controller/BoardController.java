package com.boanni_back.project.board.controller;

import com.boanni_back.project.auth.entity.CustomUserDetails;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.board.controller.dto.AllBoardsResponseDTO;
import com.boanni_back.project.board.controller.dto.SingleBoardResponseDTO;
import com.boanni_back.project.board.controller.dto.WriteBoardRequestDTO;
import com.boanni_back.project.board.entity.Board;
import com.boanni_back.project.board.repository.BoardRepository;
import com.boanni_back.project.board.service.BoardService;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/total-pages")
    public ResponseEntity<Integer> getTotalPages(@RequestParam(defaultValue = "10") int size) {
        int totalPages = boardService.getTotalPages(size);
        return ResponseEntity.ok(totalPages);
    }

    @GetMapping
    public ResponseEntity<Page<AllBoardsResponseDTO>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AllBoardsResponseDTO> response = boardService.getBoards(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleBoardResponseDTO> getSingleBoard(@PathVariable Long id){
        SingleBoardResponseDTO response = boardService.getSingleBoard(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> writeBoard(@RequestBody WriteBoardRequestDTO request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        boardService.writeBoard(request, userId);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBoard(@PathVariable Long id,
                                            @RequestBody WriteBoardRequestDTO request,
                                            Authentication authentication) {
        Long userId = extractUserId(authentication);
        boardService.updateBoard(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        boardService.deleteBoard(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
