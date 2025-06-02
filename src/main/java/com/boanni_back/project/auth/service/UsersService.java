package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.controller.dto.SignUpRequestDTO;
import com.boanni_back.project.auth.controller.dto.SignUpResponseDTO;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.EmployeeNumberRepository;
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
    private final EmployeeNumberRepository employeeNumberRepository;

    public SignUpResponseDTO saveUser(SignUpRequestDTO request, EmployeeType employeeType) {
//       비밀번호 일치 확인
        passwordCheck(request);
//        이메일중복 확인
        emailDuplicateCheck(request);

        EmployeeNumber employeeNumber = employeeNumberRepository.findByEmployeeNum(request.getEmployeeNum())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));

        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Users user = Users.builder()
                .email(request.getEmail())
                .employeeNumber(employeeNumber)
                .password(encodedPassword)
                .employeeType(employeeType)
                .build();

        usersRepository.save(user);
        return new SignUpResponseDTO(employeeNumber.getEmployeeNum(),user.getEmail());
    }




    private void emailDuplicateCheck(SignUpRequestDTO request) {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_DUPLICATE_ERROR);
        }
    }

    private void passwordCheck(SignUpRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        }
    }
}
