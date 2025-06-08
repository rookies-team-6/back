package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.ChatDto;
import com.boanni_back.project.ai.controller.dto.GlobalSummaryDto;
import com.boanni_back.project.ai.entity.GlobalSummary;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.entity.UserAiRecord;
import com.boanni_back.project.ai.repository.GlobalSummaryRepository;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.ai.repository.UserAiRecordRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class ChatService {

    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final UserAiRecordRepository userAiRecordRepository;
    private final GroupRepository groupRepository;
    private final GlobalSummaryRepository globalSummaryRepository;

    private final PromptService promptService;
    private final AiConditionService aiConditionService;

    // chat gpt로 모범답안, 채점 점수 받기
    @Transactional
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

        // 이미 AI 답변이 있는 경우 재처리 방지
        if (userRecord.getAiAnswer() != null && !userRecord.getAiAnswer().isBlank()) {
            throw new BusinessException(ErrorCode.ALREADY_ANSWERED, userId, index);
        }

        // Groq API에 전송할 프롬프트 구성
        String prompt = promptService.buildGptPrompt(question.getQuestion(), userRecord.getUserAnswer());

        // Groq API 호출
        ChatDto.Response response = aiConditionService.getChatResponse(prompt);

        // UserAiRecord와 Users 업데이트
        userRecord.setAiAnswer(response.getModel_answer());

        // 점수 누적
        user.setScore(calculateNewScore(user.getScore(), response.getScore(), question.getId()));

        // 인덱스 설정
        long nextIndex = user.getCurrentQuestionIndex() + 1;
        if (nextIndex <= questionRepository.count()) {
            // 인덱스 다음 문제 넘어감
            user.setCurrentQuestionIndex(nextIndex);
        }

        // 저장
        userAiRecordRepository.save(userRecord);
        usersRepository.save(user);

        // 문제 인덱스 증가
        long nextIndex = user.getCurrentQuestionIndex() + 1;
        if (nextIndex > questionRepository.count()){    // 다음 문제 index가 등록된 문제 개수보다 클 경우
            throw new BusinessException(ErrorCode.NO_MORE_QUESTION, nextIndex - 1);
        }

        // 문제 인덱스 다음 문제로 넘어감
        user.setCurrentQuestionIndex(nextIndex);


        return response;
    }

    private int calculateNewScore(int currentScore, int addedScore, long num) {
        // 가중 평균으로 점수 누적
        long total = (long) currentScore * (num - 1) + addedScore;
        return (int) (total / num);
    }

    // groq으로 그룹 별 요약 내용 제공
    // 요약된 것이 이미 있으면 요약된 것과 비교
    @Transactional
    public void processGroqAnswer(Long userId) {
        Long groupNum = usersRepository.findGroupNumById(userId);
        if (groupNum == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, userId);
        }
        List<UserAiRecord> userRecords = userAiRecordRepository.findAllByUsers_Id(userId);

        // 한 번에 questionId 조회
        List<Long> questionIds = userRecords.stream()
                .map(record -> record.getQuestion().getId())
                .distinct()
                .collect(Collectors.toList());

        // 한번에 question 조회
        List<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));


        // 한 번에 그룹 조회
        List<Group> groups = groupRepository.findAllByQuestionIdInAndGroupNum(questionIds, groupNum);

        Map<Long, Group> groupMap = groups.stream()
                .collect(Collectors.toMap(g -> g.getQuestion().getId(), g -> g));

        for (UserAiRecord userRecord : userRecords) {
            Long questionId = userRecord.getQuestion().getId();
            String userAnswer = userRecord.getUserAnswer();
            Question question = questionMap.get(questionId);

            Group group = groupMap.get(questionId);

            if (group != null && group.getSummary() != null) {
                String oldTitle = group.getTitle();
                String oldSummary = group.getSummary();

                String prompt = promptService.buildGroqPrompt(question.getQuestion(), oldTitle, oldSummary, userAnswer);
                ChatDto.GroqResponse response = aiConditionService.getGroqResponse(prompt);

                group.updateContent(response.getTitle(), response.getSummary());
            } else {
                if (group == null) {
                    group = Group.builder()
                            .question(question)
                            .groupNum(groupNum)
                            .build();
                    groupMap.put(questionId, group);
                }
                group.updateContent(question.getQuestion(), userAnswer);
                groupRepository.save(group);
            }
        }
    }

    // 전체 요청 조회
    @Transactional
    public List<GlobalSummaryDto.Response> processGroqAllAnswer() {
        List<Group> allGroup = groupRepository.findAll();

        Map<Long, List<Group>> groupsByQuestionId = allGroup.stream()
                .collect(Collectors.groupingBy(g -> g.getQuestion().getId()));

        List<Long> questionIds = new ArrayList<>(groupsByQuestionId.keySet());

        List<GlobalSummary> globalSummaries = globalSummaryRepository.findByQuestionIds(questionIds);

        // questionId -> GlobalSummary 매핑
        Map<Long, GlobalSummary> globalSummaryMap = globalSummaries.stream()
                .collect(Collectors.toMap(gs -> gs.getQuestion().getId(), Function.identity()));

        List<GlobalSummaryDto.Response> resultList = new ArrayList<>();

        for (Map.Entry<Long, List<Group>> entry : groupsByQuestionId.entrySet()) {
            Long questionId = entry.getKey();
            List<Group> groups = entry.getValue();

            List<String> summaries = groups.stream()
                    .map(Group::getSummary)
                    .filter(Objects::nonNull)
                    .toList();

            // 비어있으면 groq 요청 하지 않도록 함
            if (summaries.isEmpty()) continue;

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, questionId));

            // 기존 GlobalSummary 가져오기
            GlobalSummary summary = globalSummaryMap.getOrDefault(questionId,
                    GlobalSummary.builder().question(question).build());

            // summary가 1개만 있으면 그대로 넣기
            if (summaries.size() == 1) {
                String onlySummary = summaries.get(0);
                String onlyTitle = groups.get(0).getTitle();
                summary.updateContent(onlyTitle, onlySummary);
            } else {
                String prompt = promptService.buildGlobalPrompt(summaries);
                ChatDto.GroqResponse response = aiConditionService.getGroqResponse(prompt);
                summary.updateContent(response.getTitle(), response.getSummary());
            }

            globalSummaryRepository.save(summary);
            resultList.add(GlobalSummaryDto.Response.fromEntity(summary));
        }

        return resultList;
    }
}
