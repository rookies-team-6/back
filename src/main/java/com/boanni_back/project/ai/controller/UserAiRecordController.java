package com.boanni_back.project.ai.controller;


import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.service.UserAiRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-ai-record")
@RequiredArgsConstructor
public class UserAiRecordController {

    private final UserAiRecordService userAiRecordService;

    // 이용자 답변 입력
    @PostMapping
    public ResponseEntity<UserAiRecordDto.Response> saveUserAnswer(@RequestBody UserAiRecordDto.Request request) {
        UserAiRecordDto.Response response = userAiRecordService.saveUserAnswer(request);
        return ResponseEntity.ok(response);
    }
}
