package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.controller.dto.SignUpRequestDTO;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;  // Bean으로 등록된 BCryptPasswordEncoder

    public void saveUser(SignUpRequestDTO request){
        passwordCheck(request);
        emailDuplicateCheck(request);
        userDuplicateCheck(request);
        employeeTypeCheck(request);
//        SecurityConfig에서 BCryptPasswordEncoder bean 등록 필요하고
//        security config 설정한 후에 비밀번호 인코딩 진행
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Users user=Users.builder().username(request.getUsername()).email(request.getEmail()).password(encodedPassword).employeeType(request.getEmployeeType()).build();
        usersRepository.save(user);
    }

    private void employeeTypeCheck(SignUpRequestDTO request) {
        EmployeeType type = request.getEmployeeType();

        if (type != EmployeeType.TRAINEE && type != EmployeeType.EMPLOYEE) {
            throw new BusinessException(ErrorCode.AUTH_NOT_INCLUDED_EMPLOYEE_NUMBER_ERROR);
        }
    }

    private void userDuplicateCheck(SignUpRequestDTO request) {
        if(usersRepository.findByUsername(request.getUsername()).isPresent()){
            throw new BusinessException(ErrorCode.AUTH_USER_DUPLICATE_ERROR);
        }
    }

    private void emailDuplicateCheck(SignUpRequestDTO request) {
        if(usersRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BusinessException(ErrorCode.AUTH_EMAIL_DUPLICATE_ERROR);
        }
    }

    private void passwordCheck(SignUpRequestDTO request) {
        if(!request.getPassword().equals(request.getPasswordCheck())){
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        }
    }
}
