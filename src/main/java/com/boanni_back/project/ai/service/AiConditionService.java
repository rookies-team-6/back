package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AiConditionService {

    private final ChatModel chatModel;

    public ChatDto.Response getChatResponse(String userPrompt) {
        try {
            String systemInstruction = "당신은 정보보안 문제를 평가하는 채점 전문가입니다. 사용자 답변을 채점 기준에 따라 평가하고, JSON 형식으로 결과를 제공합니다.\n";
            Prompt prompt = new Prompt(systemInstruction + userPrompt);

            ChatResponse response = chatModel.call(prompt);
            JsonNode json = getJsonResponse(response);

            return ChatDto.Response.fromJson(json);

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR);

        } catch (Exception e) {
            log.error("IO 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.API_SERVER_ERROR);
        }
    }

    // GPT 응답의 JSON 추출 안정화
    private static JsonNode getJsonResponse(ChatResponse response) throws JsonProcessingException {
        String resultText = response.getResult().getOutput().getText();

        // JSON 코드 블록 제거 처리
        resultText = resultText.replaceAll("(?s)```json|```", "").trim();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(resultText);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR, "JSON 파싱 실패: " + resultText);
        }
    }

    // Groq api 응답에서 JSON 추출
//    private static JsonNode getJsonResponse(ChatResponse response) throws JsonProcessingException {
//        String resultText = response.getResult().getOutput().getText();
//
//        // JSON 부분만 추출
//        int jsonStart = resultText.indexOf('{');
//        int jsonEnd = resultText.lastIndexOf('}') + 1;
//
//        // 응답에 JSON 없을 경우
//        if (jsonStart == -1 || jsonEnd == -1) {
//            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR);
//        }
//        String jsonPart = resultText.substring(jsonStart, jsonEnd);
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode json = mapper.readTree(jsonPart);
//        return json;
//    }

}
