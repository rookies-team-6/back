package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AiConditionService {

    private final OpenAiChatModel openAiChatModel; // 구체적 타입 사용
    private final OpenAiChatModel groqChatModel;

    public AiConditionService(
            @Qualifier("openai") OpenAiChatModel openAiChatModel,
            @Qualifier("groq") OpenAiChatModel groqChatModel
    ) {
        this.openAiChatModel = openAiChatModel;
        this.groqChatModel = groqChatModel;
    }

    public ChatDto.Response getChatResponse(String userPrompt) {
        try {
            String systemInstruction = "당신은 사내에서 발생할 수 있는 보안 문제를 점검하는 보안 전문가입니다. 비유적 응답에 점수를 부여하지 않으며, 정확한 응답에만 채점을 부여합니다. 사용자 답변을 채점 기준에 따라 평가하고, JSON 형식으로 결과를 제공합니다.\n";
            Prompt prompt = new Prompt(
                    new SystemMessage(systemInstruction),
                    new UserMessage(userPrompt)
            );

            ChatResponse response = openAiChatModel.call(prompt);
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

    public ChatDto.GroqResponse getGroqResponse(String userPrompt) {
        try {
            String systemInstruction = "당신은 조직 내 보안 전문가입니다. JSON 형식으로 결과를 제공합니다.\n";
            Prompt prompt = new Prompt(
                    new SystemMessage(systemInstruction),
                    new UserMessage(userPrompt)
            );

            ChatResponse response = groqChatModel.call(prompt);
            // ✅ Groq 응답 원본 로그 추가
            log.info("Groq 응답 원본: {}", response.getResults());

            JsonNode json = getJsonResponse(response);

            // groq json 응답 확인하기
            JsonNode titleNode = json.get("title");
            JsonNode summaryNode = json.get("summary");
            log.info("Groq 응답 titleNode : " + titleNode);
            log.info("Groq 응답 summaryNode : " + summaryNode);


            return ChatDto.GroqResponse.fromJson(json);
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
        resultText = resultText.replaceAll("(?s)```json.*?```", "").trim();

        // JSON 문자열만 추출 (중괄호{}로 감싸진 부분만)
        int startIndex = resultText.indexOf("{");
        int endIndex = resultText.lastIndexOf("}");
        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR, "응답에 JSON 형식이 포함되어 있지 않습니다.");
        }
        String jsonString = resultText.substring(startIndex, endIndex + 1);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR, "JSON 파싱 실패: " + jsonString, e);
        }
    }
}
