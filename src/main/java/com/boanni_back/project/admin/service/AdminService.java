//package com.boanni_back.project.admin.service;
//
//import com.boanni_back.project.admin.entity.Admin;
//import com.boanni_back.project.admin.exception.AdminBusinessException;
//import com.boanni_back.project.admin.exception.AdminErrorCode;
//import com.boanni_back.project.admin.repository.AdminRepository;
//import com.boanni_back.project.user.entity.EmployeeType;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class AdminService {
//
//    private final AdminRepository adminRepository;
//
//    public AdminService(AdminRepository adminRepository) {
//        this.adminRepository = adminRepository;
//    }
//
//    public List<Admin> getAllUsers() {
//        return adminRepository.findAll();
//    }
//
//    public Admin getUserByEmail(String email) {
//        return adminRepository.findByEmail(email)
//                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, email));
//    }
//
//    public void deleteUserById(Long id) {
//        if (!adminRepository.existsById(id)) {
//            throw new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, id);
//        }
//        adminRepository.deleteById(id);
//    }
//
//    public void promoteUserToAdmin(Long id) {
//        Admin user = adminRepository.findById(id)
//                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, id));
//
//        user.
//        adminRepository.save(user);
//    }
//
//    public List<Admin> getUsersByEmployeeType(EmployeeType employeeType) {
//        return adminRepository.findByEmployeeType(employeeType);
//    }
//
//
//}
