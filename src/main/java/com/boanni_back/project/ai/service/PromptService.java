package com.boanni_back.project.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PromptService {

    private final ResourceLoader resourceLoader;

    public PromptService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private String loadTemplate(String path) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    // gpt prompt
    public String buildGptPrompt(String question, String userAnswer) {
        try {
            String template = loadTemplate("prompt/gptSummaryPrompt.txt");

            // 확인
            log.info("프롬프트 : " + template);
            return String.format(template, question, userAnswer);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 템플릿 로딩 실패", e);
        }
    }

    // groq prompt
    public String buildGroqPrompt(String oldTitle, String oldSummary, String userAnswer) {
        try {
            String template = loadTemplate("prompt/groqGroupPrompt.txt");

            // 확인
            log.info("프롬프트 : " + template);
            return String.format(template, oldTitle, oldSummary, userAnswer);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 템플릿 로딩 실패", e);
        }
    }

    public String buildGlobalPrompt(List<String> summaries) {
        String joinedAnswers = joinAnswers(summaries);
        try {
            String template = loadTemplate("prompt/groqGlobalSummaryPrompt.txt");

            // 확인
            log.info("프롬프트 : " + template);
            return String.format(template, joinedAnswers);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 템플릿 로딩 실패", e);
        }
    }

    private String joinAnswers(List<String> answers) {
        return answers.stream()
                .map(answer -> "- " + answer)
                .collect(Collectors.joining("\n"));
    }
}


