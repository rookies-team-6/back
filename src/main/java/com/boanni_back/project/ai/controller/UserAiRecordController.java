package com.boanni_back.project.ai.controller;


import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.service.UserAiRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-ai-record")
@RequiredArgsConstructor
public class UserAiRecordController {

    private final UserAiRecordService userAiRecordService;

    // 이용자 답변 조회
    @GetMapping
    public ResponseEntity<List<UserAiRecordDto.Response>> getUserAnswer(){
        return ResponseEntity.ok(userAiRecordService.getUserAnswer());
    }

    // 이용자 답변 등록
    @PostMapping
    public ResponseEntity<UserAiRecordDto.Response> saveUserAnswer(@RequestBody UserAiRecordDto.Request request) {
        UserAiRecordDto.Response response = userAiRecordService.saveUserAnswer(request);
        return ResponseEntity.ok(response);
    }
}
