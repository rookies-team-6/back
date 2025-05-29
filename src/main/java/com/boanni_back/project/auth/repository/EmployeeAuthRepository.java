package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAuthRepository extends JpaRepository<EmployeeAuth,Long> {
    Optional<EmployeeAuth> getEmployeeAuthByEmployeeNum(String employeeNum);
}
