package com.boanni_back.project.ai.service;

import org.springframework.stereotype.Service;

@Service
public class PromptService {
    // 프롬프트 구성
    public String buildPrompt(String question, String userAnswer) {
        return """
            해당 문제에 대해 모범답안을 작성하고, 문제에 대해 사용자가 답변을 알맞게 했는지 채점 기준대로 평가해 점수를 측정해주세요.
            답변은 아래있는 JSON 형식으로 한국어로 응답해주세요.
            채점 기준 (총 100점):
            - 기술적 조치 (45점)
            - 정책적 조치 (20점)
            - 구체성/실현 가능성 (30점)
            - 예시 포함 여부 (5점)
            
            문제:
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
}
