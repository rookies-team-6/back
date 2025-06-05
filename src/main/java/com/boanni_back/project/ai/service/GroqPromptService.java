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
                당신은 조직 내 보안 전문가입니다. 다음은 직원들의 자유응답 모음입니다. 아래 규칙을 절대 어기지 마세요:
                
                1. 직원들의 답변 중 반복되거나 의미 있는 키워드를 정리해 요약합니다.
                2. 반드시 JSON 형식으로만 응답하세요. JSON 형식은 Key값과 Value값이 모두 있어야 합니다.
                    KEY값으로는 "title", "summary"라고 명명하세요.
                3. JSON 형식으로 반드시 아래 형식만 반환하세요:
                 {
                   "title": "문자열",
                   "summary": "문자열"
                 }
                 다른 텍스트, 추가 문장 없이 JSON만 정확히 출력하세요.
                4. 모든 값은 반드시 큰따옴표(")로 감싸고, JSON 형식이 완전해야 하며, 누락이나 형식 오류가 있어선 안 됩니다.
                5. JSON 외의 텍스트, 설명, 공백, 주석, 개행, 서두, 부연설명도 포함하지 마세요.
                
                직원 답변들:
                %s
                """.formatted(joinedAnswers);
    }
}
