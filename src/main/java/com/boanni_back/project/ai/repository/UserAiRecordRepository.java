package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.UserAiRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAiRecordRepository extends JpaRepository<UserAiRecord, Long> {

    Optional<UserAiRecord> findByUsersIdAndQuestionId(Long userId, Long questionId);

    @Query("SELECT uar FROM UserAiRecord uar JOIN FETCH uar.question")
    List<UserAiRecord> findAllWithQuestion();

    @EntityGraph(attributePaths = {"question"})
    List<UserAiRecord> findByUsersIdAndQuestionIdLessThanEqualOrderByQuestionIdAsc(Long userId, Long questionId);

    @EntityGraph(attributePaths = {"question"})
    List<UserAiRecord> findByUsersIdAndIsBookMarkedTrue(Long userId);

    List<UserAiRecord> findAllByUsers_Id(Long userId);
}
