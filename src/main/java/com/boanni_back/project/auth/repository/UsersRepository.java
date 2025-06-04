package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    //Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmployeeNumber(EmployeeNumber employeeNumber);

    List<Users> findAllByDepartmentCode(@Param("departmentCode") String departmentCode);

    @Query("SELECT u.departmentCode FROM Users u WHERE u.id = :id")
    String findDepartmentCodeById(@Param("id") Long id);
}
