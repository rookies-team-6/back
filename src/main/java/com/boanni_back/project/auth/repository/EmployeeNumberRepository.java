package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeNumberRepository extends JpaRepository<EmployeeNumber,String> {
    Optional<EmployeeNumber> findByEmployeeNum(String employeeNum);


    @Query("SELECT e FROM EmployeeNumber e WHERE e.employeeNum = :employeeNum")
    Optional<EmployeeNumber> debugQuery(@Param("employeeNum") String employeeNum);
}
