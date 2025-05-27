package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.entity.EmployeeType;
import com.boanni_back.project.admin.entity.User;
import com.boanni_back.project.admin.exception.BusinessException;
import com.boanni_back.project.admin.exception.ErrorCode;
import com.boanni_back.project.admin.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<User> getAllUsers() {
        return adminRepository.findAll();
    }

    public User getUserByEmail(String email) {
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
        User user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));

        user.setEmployee_type(EmployeeType.ADMIN);
        adminRepository.save(user);
    }

    public List<User> getUsersByEmployeeType(EmployeeType employeeType) {
        return adminRepository.findByEmployeeType(employeeType);
    }


}
