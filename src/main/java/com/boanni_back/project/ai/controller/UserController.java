package com.boanni_back.project.ai.controller;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.ai.service.UserService;
import com.boanni_back.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UsersDto.UserGroupScoresResponse> getUserWithGroupScores(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        return ResponseEntity.ok(userService.getUserWithGroupScores(userId));
    }

}
