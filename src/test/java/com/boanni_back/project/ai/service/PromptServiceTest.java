package com.boanni_back.project.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class PromptServiceTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private PromptService promptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buildGptPrompt_success() throws Exception {
        String template = "질문: %s\n답변: %s";
        Resource resource = mock(Resource.class);
        given(resourceLoader.getResource("classpath:prompt/gptSummaryPrompt.txt")).willReturn(resource);
        given(resource.getInputStream()).willReturn(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)));

        String result = promptService.buildGptPrompt("Q1", "A1");

        assertThat(result).isEqualTo("질문: Q1\n답변: A1");
    }

    @Test
    void buildGroqPrompt_success() throws Exception {
        String template = "제목: %s\n요약: %s\n답변: %s";
        Resource resource = mock(Resource.class);
        given(resourceLoader.getResource("classpath:prompt/groqGroupPrompt.txt")).willReturn(resource);
        given(resource.getInputStream()).willReturn(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)));

        String result = promptService.buildGroqPrompt("title", "summary", "answer");

        assertThat(result).isEqualTo("제목: title\n요약: summary\n답변: answer");
    }

    @Test
    void buildGlobalPrompt_success() throws Exception {
        String template = "모든 답변:\n%s";
        Resource resource = mock(Resource.class);
        given(resourceLoader.getResource("classpath:prompt/groqGlobalSummaryPrompt.txt")).willReturn(resource);
        given(resource.getInputStream()).willReturn(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)));

        List<String> summaries = List.of("요약1", "요약2");
        String result = promptService.buildGlobalPrompt(summaries);

        assertThat(result).isEqualTo("모든 답변:\n- 요약1\n- 요약2");
    }

    @Test
    void buildGptPrompt_IOException_fail_exception() throws Exception {
        given(resourceLoader.getResource("classpath:prompt/gptSummaryPrompt.txt"))
                .willAnswer(invocation -> { throw new IOException("파일 없음"); });

        assertThatThrownBy(() -> promptService.buildGptPrompt("Q", "A"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("프롬프트 템플릿 로딩 실패");
    }
}
