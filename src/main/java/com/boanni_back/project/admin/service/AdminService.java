package com.boanni_back.project.admin.service;

import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Users> getAllUsers() {
        return adminRepository.findAll();
    }

    public Users getUserByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, email));
    }

    public void deleteUserById(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, id);
        }
        adminRepository.deleteById(id);
    }

    public void promoteUserToAdmin(Long id) {
        Users users = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));

        users.setEmployee_type(EmployeeType.ADMIN);
        adminRepository.save(users);
    }

    public List<Users> getUsersByEmployeeType(EmployeeType employeeType) {
        return adminRepository.findByEmployeeType(employeeType);
    }


}