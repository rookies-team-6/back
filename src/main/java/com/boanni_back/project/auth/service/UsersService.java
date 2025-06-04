package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.controller.dto.*;
import com.boanni_back.project.auth.entity.CustomUserDetails;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.EmployeeNumberRepository;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;  // Bean으로 등록된 BCryptPasswordEncoder
    private final EmployeeNumberRepository employeeNumberRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;


    public SignUpResponseDTO saveUser(SignUpRequestDTO request, EmployeeType employeeType) {
//       비밀번호 일치 확인
        passwordCheck(request);
//        이메일중복 확인
        if(isEmailDuplicate(request.getEmail())){
            throw new BusinessException(ErrorCode.AUTH_EMAIL_DUPLICATE_ERROR);
        }

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

        employeeNumber.markAsUsed();
        employeeNumberRepository.save(employeeNumber);

        return new SignUpResponseDTO(employeeNumber.getEmployeeNum(),user.getEmail());
    }

    public SignInResponseDTO signIn(SignInRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // refreshToken DB 저장
        refreshTokenService.saveOrUpdate(userDetails.getId(), refreshToken, 7);

        return new SignInResponseDTO(accessToken, "Bearer", refreshToken);
    }

    public SignInResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        String email = jwtTokenProvider.getUsername(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

        // DB에 저장된 refreshToken과 비교
        refreshTokenService.validateRefreshToken(userDetails.getId(), refreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // 새 refreshToken DB 갱신
        refreshTokenService.updateRefreshToken(userDetails.getId(), newRefreshToken, 7);

        return new SignInResponseDTO(newAccessToken, "Bearer", newRefreshToken);
    }


    public boolean isEmailDuplicate(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    private void passwordCheck(SignUpRequestDTO request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        }
    }
}
