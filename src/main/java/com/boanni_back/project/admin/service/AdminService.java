package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;

    //모든 회원 조회
    public Page<UsersDto.Response> getAllUsers(Pageable pageable) {
        long totalQuestions = questionRepository.count();
        return adminRepository.findAll(pageable)
                .map(user -> UsersDto.Response.fromEntityWithProgress(user, totalQuestions));
    }

    //해당 email 회원 조회
    public UsersDto.Response getUserByEmail(String email) {
        long totalQuestions = questionRepository.count();
        return adminRepository.findByEmail(email)
                .map(user -> UsersDto.Response.fromEntityWithProgress(user, questionRepository.count()))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL, email));
    }

    //employee_type으로 회원 조회
    public List<UsersDto.Response> getUsersByEmployeeType(EmployeeType employeeType) {
        long totalQuestions = questionRepository.count();
        return adminRepository.findByEmployeeType(employeeType).stream()
                .map(user -> UsersDto.Response.fromEntityWithProgress(user, totalQuestions))
                .collect(Collectors.toList());
    }

    //이름으로 회원 조회
    public UsersDto.Response getUserByUsername(String username) {
        List<Users> users = adminRepository.findByEmployeeNumber_UsernameContaining(username);

        if (users.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND_BY_USERNAME, username);
        }

        long totalQuestions = questionRepository.count();
        return UsersDto.Response.fromEntityWithProgress(users.get(0), totalQuestions);
    }


    //해당 id 회원 삭제
    public Users deleteUserById(Long id) {
        Users user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        adminRepository.delete(user);
        return user;
    }

    //관리자로 승격
    public Users promoteUserToAdmin(Long id) {
        Users user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        user.setEmployeeType(EmployeeType.ADMIN);
        return adminRepository.save(user);
    }
}