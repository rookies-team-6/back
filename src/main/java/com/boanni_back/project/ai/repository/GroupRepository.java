package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByQuestionIdInAndGroupNum(List<Long> questionIds, Long groupNum);

    @Query("SELECT g FROM Group g JOIN FETCH g.question WHERE g.groupNum = :groupNum")
    List<Group> findByGroupNumWithQuestion(@Param("groupNum") Long groupNum);
}
