package com.boanni_back.project.ai.controller.dto;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

public class ChatDto {

    @Data
    @AllArgsConstructor
    public static class Response {
        private int score;
        private String status;
        private String model_answer;
        private String feedback;

        // JSON 파싱 → DTO 변환 팩토리 메서드
        public static Response fromJson(JsonNode json) {
            return new Response(
                    json.get("score").asInt(),
                    "success",
                    json.get("model_answer").asText(),
                    json.get("feedback").asText()
            );
        }
    }

    @Data
    @AllArgsConstructor
    public static class GroqResponse {
        private String title;
        private String summary;

        // JSON 파싱 → DTO 변환 팩토리 메서드
        public static GroqResponse fromJson(JsonNode json) {
            String title = Optional.ofNullable(json.get("title")).map(JsonNode::asText).orElse(null);
            String summary = Optional.ofNullable(json.get("summary")).map(JsonNode::asText).orElse(null);

            return new GroqResponse(title, summary);
        }
    }
}
