package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.ai.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // test : json으로 prompt 요청 -> 응답
    @PostMapping("/test/{userId}")
    public ResponseEntity<ChatDto.Response> testGroq(@PathVariable Long userId) {
        ChatDto.Response response = chatService.processChatAnswer(userId);
        return ResponseEntity.ok(response);
    }
}