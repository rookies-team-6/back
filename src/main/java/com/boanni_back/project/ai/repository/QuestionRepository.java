package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByQuestionContaining(String keyword, Pageable pageable);
    long count();
}
