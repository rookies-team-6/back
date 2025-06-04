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
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmployeeNumber(EmployeeNumber employeeNumber);
    List<Users> findAllByGroupNum(Long groupNum);

    @Query("SELECT u.groupNum FROM Users u WHERE u.id = :id")
    Long findGroupNumById(@Param("id") Long id);

    @Query("SELECT u FROM Users u JOIN FETCH u.employeeNumber WHERE u.email = :email")
    Optional<Users> findByEmailWithEmployeeNumber(@Param("email") String email);

    // groupNum별로 groupScore(총합) 구하기
    @Query("SELECT u.groupNum, SUM(u.score) FROM Users u GROUP BY u.groupNum")
    List<Object[]> findAllGroupScores();
}
