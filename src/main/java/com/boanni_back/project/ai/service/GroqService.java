package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class GroqService {

    private final ChatModel chatModel;

    public ChatDto.Response getChatResponse(String userPrompt) {
        try {
            String systemInstruction = "당신은 보안 문제 채점관입니다.\n";
            Prompt prompt = new Prompt(systemInstruction + userPrompt);

            ChatResponse response = chatModel.call(prompt);
            JsonNode json = getJsonResponse(response);

            return ChatDto.Response.fromJson(json);

        } catch (Exception e) {
            e.printStackTrace(); // 로그 출력
            throw new BusinessException(ErrorCode.API_SERVER_ERROR, e.getMessage());
        }
    }

    // Groq api 응답에서 JSON 추출
    private static JsonNode getJsonResponse(ChatResponse response) throws JsonProcessingException {
        String resultText = response.getResult().getOutput().getText();

        // JSON 부분만 추출
        int jsonStart = resultText.indexOf('{');
        int jsonEnd = resultText.lastIndexOf('}') + 1;

        // 응답에 JSON 없을 경우
        if (jsonStart == -1 || jsonEnd == -1) {
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR);
        }
        String jsonPart = resultText.substring(jsonStart, jsonEnd);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonPart);
        return json;
    }
}
