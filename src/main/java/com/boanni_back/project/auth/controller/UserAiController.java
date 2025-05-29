package com.boanni_back.project.auth.controller;

import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.service.UserAiRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
