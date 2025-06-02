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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;
    private final UserAiRecordRepository userAiRecordRepository;
    //private final GroqPromptService promptService;
    private final GptPromptService gptPromptService;
    private final AiConditionService aiConditionService;

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
        String prompt = gptPromptService.buildPrompt(question.getQuestion(), userRecord.getUserAnswer());

        // Groq API 호출
         ChatDto.Response response = aiConditionService.getChatResponse(prompt);

        // UserAiRecord와 Users 업데이트
        userRecord.setAiAnswer(response.getModel_answer());

        // 점수 누적
        user.setScore(calculateNewScore(user.getScore(), response.getScore(), question.getId()));

        // 저장
        userAiRecordRepository.save(userRecord);
        adminRepository.save(user);

        return response;
    }

    private int calculateNewScore(int currentScore, int addedScore, long num) {
        // 가중 평균으로 점수 누적
        long total = (long)currentScore * (num-1) + addedScore;
        return (int)(total / num);
    }
}
