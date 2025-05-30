package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAuthRepository extends JpaRepository<EmployeeNumber,Long> {
    Optional<EmployeeNumber> getEmployeeAuthByEmployeeNum(String employeeNum);
}
