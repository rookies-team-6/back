package com.boanni_back.project.auth.controller;

import com.boanni_back.project.auth.controller.dto.*;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.service.EmployeeAuthService;
import com.boanni_back.project.auth.service.UsersService;
import com.boanni_back.project.jwt.JwtTokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController  {
    private final EmployeeAuthService employeeAuthService;
    private final UsersService usersService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

//    사원 번호 확인 컨트롤러
    @GetMapping("/verify")
    public ResponseEntity<VerifyResponseDTO> verifyEmployeeNum(@ModelAttribute VerifyRequestDTO request){
        VerifyResponseDTO response=employeeAuthService.verifyEmployeeAuth(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO request){
        EmployeeType employeeType=employeeAuthService.getEmployeeType(request.getEmployeeNum());
        SignUpResponseDTO response=usersService.saveUser(request,employeeType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(userDetails.getUsername());
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2) 인증 성공 시 JWT 생성
        String token = jwtTokenProvider.generateToken(authentication);

        SignInResponseDTO response = new SignInResponseDTO(token, "Bearer");

        // 3) 토큰 응답
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/email/check")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email){
        boolean isDuplicate = usersService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }
}

