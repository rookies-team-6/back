package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByQuestionIdAndDepartmentCode(Long questionId, String departmentCode);
}
