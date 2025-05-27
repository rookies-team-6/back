package com.boanni_back.project.admin.repository;

import com.boanni_back.project.admin.entity.EmployeeType;
import com.boanni_back.project.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.employee_type = :type")
    List<User> findByEmployeeType(@Param("type") EmployeeType employeeType);
}
