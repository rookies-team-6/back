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
    public List<UsersDto.Response> getAllUsers() {
        return adminRepository.findAll().stream()
                .map(UsersDto.Response::fromEntity)
                .toList();
    }

    //해당 email 회원 조회
    public UsersDto.Response getUserByEmail(String email) {
        return adminRepository.findByEmail(email)
                .map(UsersDto.Response::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
    }

    //employee_type으로 회원 조회
    public List<UsersDto.Response> getUsersByEmployeeType(EmployeeType employeeType) {
        return adminRepository.findByEmployeeType(employeeType).stream()
                .map(UsersDto.Response::fromEntity)
                .toList();
    }

    //해당 id 회원 삭제
    public Users deleteUserById(Long id) {
        Users user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        adminRepository.delete(user);
        return user;
    }

    public Users promoteUserToAdmin(Long id) {
        Users user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        user.setEmployeeType(EmployeeType.ADMIN);
        return adminRepository.save(user);
    }
}