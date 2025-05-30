package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.UserAiRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAiRecordRepository extends JpaRepository<UserAiRecord, Long> {

    Optional<UserAiRecord> findByUsersIdAndQuestionId(Long userId, Long questionId);
}
