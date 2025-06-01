package com.boanni_back.project.ai.controller;


import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.service.UserAiRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
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

    // 북마크 체크
    @PostMapping("/bookmarked")
    public ResponseEntity<UserAiRecordDto.Response> saveBookmarked(@RequestBody UserAiRecordDto.BookmarkedRequest request){
        UserAiRecordDto.Response response = userAiRecordService.saveBookedmarked(request);
        return ResponseEntity.ok(response);
    }

    // 자신이 푼 문제 조회
    @GetMapping("/solved/{userId}")
    public ResponseEntity<List<UserAiRecordDto.Response>> getSolvedRecord(@PathVariable Long userId){
        return ResponseEntity.ok(userAiRecordService.getSolvedRecord(userId));
    }

    // 푼 문제 중 북마크한 문제 조회
    @GetMapping("/bookmarked/{userId}")
    public ResponseEntity<List<UserAiRecordDto.Response>> getBookMarkedRecord(@PathVariable Long userId){
        return ResponseEntity.ok(userAiRecordService.getBookMarkedRecord(userId));
    }
}
