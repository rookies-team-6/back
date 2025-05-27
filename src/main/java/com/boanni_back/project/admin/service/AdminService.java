package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.exception.AdminBusinessException;
import com.boanni_back.project.admin.exception.AdminErrorCode;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.user.entity.EmployeeType;
import com.boanni_back.project.user.entity.User;
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
                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, email));
    }

    public void deleteUserById(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, id);
        }
        adminRepository.deleteById(id);
    }

    public void promoteUserToAdmin(Long id) {
        User user = adminRepository.findById(id)
                .orElseThrow(() -> new AdminBusinessException(AdminErrorCode.USER_NOT_FOUND, id));

        user.setEmployee_type(EmployeeType.ADMIN);
        adminRepository.save(user);
    }

    public List<User> getUsersByEmployeeType(EmployeeType employeeType) {
        return adminRepository.findByEmployeeType(employeeType);
    }


}
