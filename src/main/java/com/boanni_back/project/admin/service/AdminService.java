package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    //모든 회원 조회
    public List<UsersDto> getAllUsers() {
        return adminRepository.findAll().stream()
                .map(UsersDto::fromEntity)
                .toList();
    }

    //해당 email 회원 조회
    public UsersDto getUserByEmail(String email) {
        return adminRepository.findByEmail(email)
                .map(UsersDto::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, email));
    }

    //employee_type으로 회원 조회
    public List<UsersDto> getUsersByEmployeeType(EmployeeType employeeType) {
        return adminRepository.findByEmployeeType(employeeType).stream()
                .map(UsersDto::fromEntity)
                .toList();
    }

    //해당 id 회원 삭제
    public void deleteUserById(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, id);
        }
        adminRepository.deleteById(id);
    }

    public void promoteUserToAdmin(Long id) {
        Users users = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));

        users.setEmployeeType(EmployeeType.ADMIN);
        adminRepository.save(users);
    }
    
}