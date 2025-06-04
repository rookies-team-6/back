package com.boanni_back.project.ai.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroqPromptService {

    // 그룹 요약 프롬프트 구성
    public String buildPrompt(List<String> answers) {

        // List<String> 형식 String으로 변경
        String joinedAnswers = answers.stream()
                .map(answer -> "- " + answer)
                .collect(Collectors.joining("\n"));

        // 프롬프트
        return """
            다음은 부서 직원들이 문제에 대한 답변들을 모은 것입니다. 이 내용의 빈도가 많은 문장이나 중요한 점을 정리하여 주세요:
           
            직원들의 답변들:
            %s
           
            {
              "title": "중요 내용 정리",
              "summary": "내용"
            }
            """.formatted(joinedAnswers);
    }
}
