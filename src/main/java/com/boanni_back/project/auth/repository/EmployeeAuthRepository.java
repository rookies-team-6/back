package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAuthRepository extends JpaRepository<EmployeeAuth,String> {
    Optional<EmployeeAuth> findByEmployeeNum(String employeeNum);

    @Query("SELECT e FROM EmployeeAuth e WHERE e.employeeNum = :employeeNum")
    Optional<EmployeeAuth> debugQuery(@Param("employeeNum") String employeeNum);
}
