package com.boanni_back.project.ai.controller;

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
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testGroq(@RequestBody String prompt) {
        Map<String, String> result = chatService.getChatResponse(prompt);

        if ("success".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.internalServerError().body(result);
        }
    }
}