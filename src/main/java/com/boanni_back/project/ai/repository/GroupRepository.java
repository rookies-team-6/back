package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByQuestionIdAndGroupNum(Long questionId, Long groupNum);
    Optional<Group> findByGroupNum(Long groupNum);
}
