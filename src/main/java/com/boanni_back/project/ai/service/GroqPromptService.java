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
                당신은 조직 내 보안 전문가입니다. 다음은 직원들의 답변 모음입니다. 아래 규칙을 엄격히 지켜 응답하세요:
                
                1. 자주 언급된 키워드와 핵심 내용을 분석해 요약하세요.
                2. 반드시 정확한 JSON 형식으로 출력하세요.
                3. 모든 문자열은 반드시 큰따옴표(")로 감싸야 하며, 닫는 따옴표가 절대 빠지지 않아야 합니다. 이 점 꼭 유의해주세요.
                4. 모든 문장은 마침표(.)로 끝내세요.
                5. JSON 외에 다른 텍스트, 설명, 공백, 줄바꿈을 포함하지 마세요.
                
                6. 아래 예시처럼 출력하세요:
                {
                  "title": "EDR와 행위 기반 탐지 솔루션",
                  "summary": "직원들은 탐지를 가능하도록 하며, 무슨 보안을 강화할 것을 제안합니다."
                }
                
                직원 답변들:
                %s
                """.formatted(joinedAnswers);
    }
}
