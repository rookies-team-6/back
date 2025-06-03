package com.boanni_back.project.auth.repository;

import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    //Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmployeeNumber(EmployeeNumber employeeNumber);

    @Query("SELECT u FROM Users u JOIN FETCH u.employeeNumber WHERE u.email = :email")
    Optional<Users> findByEmailWithEmployeeNumber(@Param("email") String email);

}
