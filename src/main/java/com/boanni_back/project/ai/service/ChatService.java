package com.boanni_back.project.ai.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.entity.UserAiRecord;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.ai.repository.UserAiRecordRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatModel chatModel;
    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;
    private final UserAiRecordRepository userAiRecordRepository;

    // 기본
    public ChatDto.Response processChatAnswer(Long userId) {
        // 사용자 정보 조회
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Long index = user.getCurrentQuestionIndex();

        // 문제 인덱스로 질문지 가져오기
        Question question = questionRepository.findById(index)
                .orElseThrow(() -> new BusinessException(ErrorCode.INDEX_NOT_FOUND, index));

        // UserAiRecord에서 사용자 답변 가져오기
        UserAiRecord userRecord = userAiRecordRepository.findByUsersIdAndQuestionId(userId, question.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, userId, index));

        // Groq API에 전송할 프롬프트 구성
        String prompt = buildPrompt(question.getQuestion(), userRecord.getUserAnswer());

        // Groq API 호출
         ChatDto.Response response = getChatResponse(prompt);

        // UserAiRecord와 Users 업데이트
        userRecord.setAiAnswer(response.getModel_answer());

        // 점수 누적 - 수정 필요
        user.setScore(user.getScore() + response.getScore());

        // 저장
        userAiRecordRepository.save(userRecord);
        adminRepository.save(user);

        return response;
    }

    // 프롬프트 구성
    private String buildPrompt(String question, String userAnswer) {
        return """
            다음 질문에 대해 모범답안을 작성하고 사용자 답변을 평가하여 채점 기준대로 점수를 측정해주세요.
            답변은 아래있는 JSON 형식으로 한국어로 응답해주세요.
            채점 기준 (총 100점):
            - 기술적 조치 (45점)
            - 정책적 조치 (20점)
            - 구체성/실현 가능성 (30점)
            - 예시 포함 여부 (5점)
            
            질문:
            %s
            사용자 답변:
            %s
            
            {
              "model_answer": "내용 요약 + 예시(선택)",
              "score": 숫자 (0~100),
              "feedback": "간결한 피드백"
            }
            """.formatted(question, userAnswer);
    }

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
