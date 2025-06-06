package com.boanni_back.project.ai.repository;

import com.boanni_back.project.ai.entity.GlobalSummary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GlobalSummaryRepository extends JpaRepository<GlobalSummary, Long> {
    @EntityGraph(attributePaths = {"question"})
    @Query("SELECT gs FROM GlobalSummary gs WHERE gs.question.id IN :ids")
    List<GlobalSummary> findByQuestionIds(@Param("ids") List<Long> ids);
}
