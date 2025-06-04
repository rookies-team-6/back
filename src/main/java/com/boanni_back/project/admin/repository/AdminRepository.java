package com.boanni_back.project.admin.repository;

import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.employeeType = :type")
    List<Users> findByEmployeeType(@Param("type") EmployeeType employeeType);

    List<Users> findAllByOrderByScoreDesc();

    @Query("SELECT u.currentQuestionIndex FROM Users u WHERE u.id = :id")
    Long findCurrentQuestionIndexById(@Param("id") Long id);
  
    List<Users> findByQuestionSolveDeadlineBefore(LocalDate date);

    Page<Users> findAll(Pageable pageable);

    List<Users> findByEmployeeNumber_UsernameContaining(String username);
}
