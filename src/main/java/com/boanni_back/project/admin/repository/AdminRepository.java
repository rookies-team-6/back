//package com.boanni_back.project.admin.repository;
//
//import com.boanni_back.project.admin.entity.Admin;
//import com.boanni_back.project.user.entity.EmployeeType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface AdminRepository extends JpaRepository<Admin, Long> {
//    Optional<Admin> findByEmail(String email);
//
//    @Query("SELECT u FROM User u WHERE u.employee_type = :type")
//    List<Admin> findByEmployeeType(@Param("type") EmployeeType employeeType);
//}
