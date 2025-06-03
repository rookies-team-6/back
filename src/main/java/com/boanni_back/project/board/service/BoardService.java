package com.boanni_back.project.board.service;

import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.board.controller.dto.AllBoardsResponseDTO;
import com.boanni_back.project.board.controller.dto.SingleBoardResponseDTO;
import com.boanni_back.project.board.controller.dto.WriteBoardRequestDTO;
import com.boanni_back.project.board.entity.Board;
import com.boanni_back.project.board.repository.BoardRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;

    public Page<AllBoardsResponseDTO> getBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Board> boards = boardRepository.findAll(pageable);

        if (page >= boards.getTotalPages() && boards.getTotalElements() != 0) {
            throw new BusinessException(ErrorCode.BOARD_PAGE_OUT_ERROR);
        }
        return boards.map(AllBoardsResponseDTO::new);
    }

    // 단일 게시글 조회
    public SingleBoardResponseDTO getSingleBoard(Long id) {
        Board board = boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));
        return new SingleBoardResponseDTO(board);
    }

    // 게시글 작성
    public void writeBoard(WriteBoardRequestDTO request, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_NOT_FOUND_BY_ID));

        Board board = Board.builder()
                .title(request.getTitle())
                .contents(request.getContents())
                .users(user)
                .build();

        boardRepository.save(board);
    }

    // 게시글 수정
    public void updateBoard(Long id, WriteBoardRequestDTO request, Long userId) {
        Board board = boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));

        if (!board.getUsers().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN_USER);
        }

        board.setTitle(request.getTitle());
        board.setContents(request.getContents());
        boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long id, Long userId) {
        Board board = boardRepository.findByIdWithUser(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND_BY_ID));

        if (!board.getUsers().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN_USER);
        }

        boardRepository.delete(board);
    }

    public int getTotalPages(int size) {
        long totalElements = boardRepository.count();
        return (int) ((totalElements + size - 1) / size);
    }

}
