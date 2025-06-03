package com.boanni_back.project.board.repository;

import com.boanni_back.project.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {
    @Query("SELECT b FROM Board b JOIN FETCH b.users u JOIN FETCH u.employeeNumber en WHERE b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);
}
