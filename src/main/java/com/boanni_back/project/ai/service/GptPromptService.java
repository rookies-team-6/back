package com.boanni_back.project.ai.service;

import org.springframework.stereotype.Service;

@Service
public class GptPromptService {
    // 프롬프트 구성
    public String buildPrompt(String question, String userAnswer) {
        return """
                아래 질문과 사용자 답변을 채점 기준에 따라 평가하고, 아래 JSON 형식으로 한국어로 작성해주세요.
                
                채점 기준:
                전문성&정확성: 30점 (질문에 대한 올바른 답변)
                기술적 조치: 30점
                정책적 조치: 20점
                구체성 및 실현 가능성: 20점
                예시 포함 여부: 추가 점수 5점
                
                {
                  "model_answer": "내용 요약 및 예시(선택)",
                  "score": 숫자 (0~100),
                  "feedback": "간결한 피드백"
                }
                
                질문: %s
                답변: %s
                """.formatted(question, userAnswer);
    }
}
