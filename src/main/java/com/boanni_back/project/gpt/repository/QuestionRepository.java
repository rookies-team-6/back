package com.boanni_back.project.gpt.repository;

import com.boanni_back.project.gpt.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
