package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.entity.UserAiRecord;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.ai.repository.UserAiRecordRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final UserAiRecordRepository userAiRecordRepository;
    private final GroupRepository groupRepository;

    private final GptPromptService gptPromptService;
    private final GroqPromptService groqPromptService;
    private final AiConditionService aiConditionService;

    // 기본
    public ChatDto.Response processChatAnswer(Long userId) {
        // 사용자 정보 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Long index = user.getCurrentQuestionIndex();

        // 문제 인덱스로 질문지 가져오기
        Question question = questionRepository.findById(index)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, index));

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
        usersRepository.save(user);

        return response;
    }

    private int calculateNewScore(int currentScore, int addedScore, long num) {
        // 가중 평균으로 점수 누적
        long total = (long)currentScore * (num-1) + addedScore;
        return (int)(total / num);
    }

    // 그룹 별 키워드 지정
    public void processGroqAnswer(Long userId) {

        // userId로 departmentCode 가져오기
        String departmentCode = usersRepository.findDepartmentCodeById(userId);
        if (departmentCode == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, userId);
        }

        List<Users> departmentUsers = usersRepository.findAllByDepartmentCode(departmentCode);

        // user로 user가 답한 질문들 모두 가져오기
        Map<Long, List<String>> answersGroupedByQuestion = userAiRecordRepository.findAllByUsersIn(departmentUsers)
                .stream()
                .collect(Collectors.groupingBy(
                        record -> record.getQuestion().getId(),  // questionId 기준으로 나눔
                        Collectors.mapping(UserAiRecord::getUserAnswer, Collectors.toList())
                ));

        // 각 질문에 대해 Groq 응답 생성
        ChatDto.GroqResponse lastResponse = null;   // 임시, 수정하기!
        for (Map.Entry<Long, List<String>> entry : answersGroupedByQuestion.entrySet()) {
            Long questionId = entry.getKey();
            List<String> answers = entry.getValue();

            String prompt = groqPromptService.buildPrompt(answers);
            ChatDto.GroqResponse response = aiConditionService.getGroqResponse(prompt);

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, questionId));

            Optional<Group> existingGroup = groupRepository.findByQuestionIdAndDepartmentCode(questionId, departmentCode);

            Group group;
            if (existingGroup.isPresent()) {
                group = existingGroup.get();
                group.updateContent(response.getTitle(), response.getSummary());
            } else {
                group = Group.builder()
                        .title(response.getTitle())
                        .summary(response.getSummary())
                        .question(question)
                        .departmentCode(departmentCode)
                        .build();
            }
            groupRepository.save(group);
        }
    }

}
