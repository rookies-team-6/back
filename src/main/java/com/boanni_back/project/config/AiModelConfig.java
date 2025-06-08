package com.boanni_back.project.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiModelConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openaiApiKey;

    @Value("${spring.ai.groq.api-key}")
    private String groqApiKey;

    @Bean(name = "groq")
    public OpenAiChatModel groqModel() {
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://api.groq.com/openai")
                        .apiKey(groqApiKey)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("llama3-70b-8192")
                        .temperature(0.7)
                        .stop(List.of("metadata", "```", "\n\n", "###"))
                        .build())
                .build();
    }

    @Bean(name = "openai")
    public OpenAiChatModel openAiModel() {
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://api.openai.com")
                        .apiKey(openaiApiKey)
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1-nano")
                        .build())
                .build();
    }
}
// model 설정, 둘 중에 하나만 사용하기
// 가장 저렴한 모델 사용 : gpt-3.5-turbo
// gpt-4보다 싸고 빠름 : gpt-4o
