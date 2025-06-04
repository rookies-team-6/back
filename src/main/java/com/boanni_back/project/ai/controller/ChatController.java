package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.ai.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // chat gpt api 요청
    @PostMapping("/gpt/{userId}")
    public ResponseEntity<ChatDto.Response> getGptAnswer(@PathVariable Long userId) {
        ChatDto.Response response = chatService.processChatAnswer(userId);
        return ResponseEntity.ok(response);
    }

    // groq api 요청
    @PostMapping("/groq/{userId}")
    public ResponseEntity<Map<String, String>> getGroqAnswer(@PathVariable Long userId) {
        chatService.processGroqAnswer(userId);
        return ResponseEntity.ok(Collections.singletonMap("message", "ok"));
    }
}