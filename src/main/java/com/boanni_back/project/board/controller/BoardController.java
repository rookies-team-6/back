package com.boanni_back.project.board.controller;

import com.boanni_back.project.auth.entity.CustomUserDetails;

import com.boanni_back.project.board.controller.dto.AllBoardsResponseDTO;
import com.boanni_back.project.board.controller.dto.SingleBoardResponseDTO;
import com.boanni_back.project.board.controller.dto.WriteBoardRequestDTO;

import com.boanni_back.project.board.controller.dto.WriteBoardResponseDTO;
import com.boanni_back.project.board.service.BoardService;

import com.boanni_back.project.util.SecurityUtil;
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
    public ResponseEntity<SingleBoardResponseDTO> getSingleBoard(@PathVariable Long id,Authentication authentication){
        Long userId = SecurityUtil.extractUserId(authentication);
        SingleBoardResponseDTO response = boardService.getSingleBoard(id,userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WriteBoardResponseDTO> writeBoard(@RequestBody WriteBoardRequestDTO request, Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        WriteBoardResponseDTO response = boardService.writeBoard(request, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBoard(@PathVariable Long id,
                                            @RequestBody WriteBoardRequestDTO request,
                                            Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        Long response=boardService.updateBoard(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteBoard(@PathVariable Long id, Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        Long response=boardService.deleteBoard(id, userId);
        return ResponseEntity.ok(response);
    }

//    private Long extractUserId(Authentication authentication) {
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        return userDetails.getId();
//    }
}
