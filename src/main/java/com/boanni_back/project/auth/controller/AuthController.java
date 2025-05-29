package com.boanni_back.project.auth.controller;

import com.boanni_back.project.auth.controller.dto.SignUpRequestDTO;
import com.boanni_back.project.auth.service.EmployeeAuthService;
import com.boanni_back.project.auth.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController  {
    private final EmployeeAuthService employeeAuthService;
    private final UsersService usersService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmployeeNum(@RequestParam("employeenum") String employeeNum){
        employeeAuthService.verifyEmployeeAuth(employeeNum);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO request){
        usersService.saveUser(request);
        return ResponseEntity.ok("회원가입 완료");
    }
}

