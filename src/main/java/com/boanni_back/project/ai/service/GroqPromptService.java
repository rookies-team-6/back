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

    // 그룹 요약 프롬프트 구성
    public String buildPrompt2(String oldTitle, String oldSummary, String userAnswer) {
        // 프롬프트
        return """
                당신은 조직 내 보안 전문가입니다. 다음은 직원 답변 요약본과 사용자 응답입니다. 아래 규칙을 절대 어기지 마세요:
                
                1. 기존에 있던 요약본에 사용자 응답을 추가해 요약합니다.
                기존 제목(title) : %s
                요약본 : %s
                사용자 응답 : %s
                
                2. title은 기존에 있던 title에서 사용자 응답 내용을 반영해서 적용합니다.
                3. 직원들의 답변 중 반복되거나 의미 있는 키워드를 정리해 요약합니다.
                4. 반드시 JSON 형식으로만 응답하세요. JSON 형식은 Key값과 Value값이 모두 있어야 합니다.
                    KEY값으로는 "title", "summary"라고 명명하세요.
                5. JSON 형식으로 반드시 아래 형식만 반환하세요:
                 {
                   "title": "문자열",
                   "summary": "문자열"
                 }
                 다른 텍스트, 추가 문장 없이 JSON만 정확히 출력하세요.
                6. 모든 값은 반드시 큰따옴표(")로 감싸고, JSON 형식이 완전해야 하며, 누락이나 형식 오류가 있어선 안 됩니다.
                7. JSON 외의 텍스트, 설명, 공백, 주석, 개행, 서두, 부연설명도 포함하지 마세요.
                8. 직원 답변 요약본과 사용자 응답에 영어가 있어도 꼭 한글로 된 결과를 반환하세요.
                """.formatted(oldTitle, oldSummary, userAnswer);
    }

    public String buildGlobalPrompt(List<String> summaries) {
        // List<String> 형식 String으로 변경
        String joinedAnswers = summaries.stream()
                .map(answer -> "- " + answer)
                .collect(Collectors.joining("\n"));

        // 프롬프트
        return """
                당신은 조직 내 보안 전문가입니다. 다음은 그룹별 응답입니다. 아래 규칙을 절대 어기지 마세요:
                
                1. 그룹별 응답에서 반복되거나 의미 있는 키워드를 정리해 요약합니다.
                그룹별 응답:
                %s
                
                2. title은 그룹별 응답 요약과 어울리는 제목을 적용합니다.
                3. 반드시 JSON 형식으로만 응답하세요. JSON 형식은 Key값과 Value값이 모두 있어야 합니다.
                    KEY값으로는 "title", "summary"라고 명명하세요.
                5. JSON 형식으로 반드시 아래 형식만 반환하세요:
                 {
                   "title": "문자열",
                   "summary": "문자열"
                 }
                 다른 텍스트, 추가 문장 없이 JSON만 정확히 출력하세요.
                6. 모든 값은 반드시 큰따옴표(")로 감싸고, JSON 형식이 완전해야 하며, 누락이나 형식 오류가 있어선 안 됩니다.
                7. JSON 외의 텍스트, 설명, 공백, 주석, 개행, 서두, 부연설명도 포함하지 마세요.
                8. 응답이 영어라면 꼭 한글로 번역해서 반환하세요.
                """.formatted(joinedAnswers);
    }
}
