package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional
public class AiConditionService {

    private final OpenAiChatModel openAiChatModel;
    private final OpenAiChatModel groqChatModel;

    public AiConditionService(
            @Qualifier("openai") OpenAiChatModel openAiChatModel,
            @Qualifier("groq") OpenAiChatModel groqChatModel
    ) {
        this.openAiChatModel = openAiChatModel;
        this.groqChatModel = groqChatModel;
    }

    public ChatDto.Response getChatResponse(String userPrompt) {
        String systemInstruction = "당신은 사내에서 발생할 수 있는 보안 문제를 점검하는 보안 전문가입니다. 비유적 응답에 점수를 부여하지 않으며, 정확한 응답에만 채점을 부여합니다. 사용자 답변을 채점 기준에 따라 평가하고, JSON 형식으로 결과를 제공합니다.\n";
        JsonNode json = callAiModel(openAiChatModel, systemInstruction, userPrompt);

        return ChatDto.Response.fromJson(json);
    }

    public ChatDto.GroqResponse getGroqResponse(String userPrompt) {
        String systemInstruction = "당신은 조직 내 보안 전문가입니다. JSON 형식으로 결과를 제공합니다.\n";
        JsonNode json = callAiModel(groqChatModel, systemInstruction, userPrompt);

        // groq json 응답 확인용 로그 (삭제 가능)
        log.info("Groq 응답 titleNode : {}", json.get("title"));
        log.info("Groq 응답 summaryNode : {}", json.get("summary"));

        return ChatDto.GroqResponse.fromJson(json);
    }

    private JsonNode callAiModel(OpenAiChatModel model, String systemInstruction, String userPrompt) {
        try {
            Prompt prompt = new Prompt(
                    new SystemMessage(systemInstruction),
                    new UserMessage(userPrompt)
            );

            ChatResponse response = model.call(prompt);
            log.info("chat 결과 : " + response);
            return parseJsonFromResponse(response);

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.API_RESPONSE_TYPE_ERROR);

        } catch (Exception e) {
            log.error("IO 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.API_SERVER_ERROR);
        }
    }

    private static JsonNode parseJsonFromResponse(ChatResponse response) throws JsonProcessingException {
        String resultText = response.getResult().getOutput().getText();

        // JSON 코드 블록 제거
        resultText = resultText.replaceAll("(?s)```json.*?```", "").trim();

        // JSON 문자열 추출
        int startIndex = resultText.indexOf("{");
        int endIndex = resultText.lastIndexOf("}");
        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            throw new BusinessException(ErrorCode.API_REVERSE_JSON_ERROR);
        }
        String jsonString = resultText.substring(startIndex, endIndex + 1);

        ObjectMapper mapper = new ObjectMapper();
        // unknown 필드 무시
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readTree(jsonString);
    }
}