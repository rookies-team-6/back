package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.GlobalSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalSummaryRepository extends JpaRepository<GlobalSummary, Long> {

    Optional<GlobalSummary> findByQuestionId(Long questionId);
}
