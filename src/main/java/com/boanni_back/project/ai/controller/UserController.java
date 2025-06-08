package com.boanni_back.project.ai.controller;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.ai.service.HomeService;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class UserController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<UsersDto.UserGroupScoresResponse> getUserWithGroupScores(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        return ResponseEntity.ok(homeService.getUserWithGroupScores(userId));
    }

    @GetMapping("/admin-check")
    public ResponseEntity<Void> redirectIfAdmin(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);

        Users user = homeService.getUserEntityById(userId);

        if (user.getEmployeeType() == EmployeeType.ADMIN) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("https://server.boaniserver.kro.kr/admin/users"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
