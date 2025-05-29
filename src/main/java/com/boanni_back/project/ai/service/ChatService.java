package com.boanni_back.project.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatModel chatModel;

    public Map<String, String> getChatResponse(String userPrompt) {
        try {
            String systemInstruction = "당신은 보안 문제 채점관입니다.\n";
            Prompt prompt = new Prompt(systemInstruction + userPrompt);

            ChatResponse response = chatModel.call(prompt);
            String resultText = response.getResult().getOutput().getText();

            return Map.of(
                    "status", "success",
                    "response", resultText
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "error",
                    "message", "Failed to process request: " + e.getMessage()
            );
        }
    }
}
