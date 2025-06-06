package com.boanni_back.project.auth.controller;

import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.service.UserAiRecordService;
import com.boanni_back.project.auth.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/ai")
@RequiredArgsConstructor
public class UserAiController {

    private final UserAiRecordService userAiRecordService;

    @PostMapping()
    public ResponseEntity<UserAiRecordDto.Response> saveUserAnswer(
            @RequestBody UserAiRecordDto.Request requestDto){
        return ResponseEntity.ok(userAiRecordService.saveUserAnswer(requestDto));
    }

}
