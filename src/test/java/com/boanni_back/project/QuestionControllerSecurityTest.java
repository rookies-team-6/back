package com.boanni_back.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boanni_back.project.config.SecurityConfig;
import com.boanni_back.project.gpt.controller.QuestionController;
import com.boanni_back.project.gpt.controller.dto.QuestionDto;
import com.boanni_back.project.gpt.service.QuestionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(QuestionController.class)
@Import({SecurityConfig.class, QuestionControllerSecurityTest.TestConfig.class})  // 수동 빈 등록을 위해 Import
public class QuestionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private QuestionService questionService;

    private static final String QUESTION_JSON = "{\"title\":\"test question\", \"content\":\"test content\"}";

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuestion_withAdminRole_shouldReturnOk() throws Exception {
        Mockito.when(questionService.createQuestion(Mockito.any()))
                .thenReturn(new QuestionDto.Response(/* 필요한 필드 값 */));

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(QUESTION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test @Disabled
    @WithMockUser(roles = "USER")
    void createQuestion_withUserRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(QUESTION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test @Disabled
    void createQuestion_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(QUESTION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Configuration @Disabled
    static class TestConfig {
        @Bean
        public QuestionService questionService() {
            return Mockito.mock(QuestionService.class);
        }
    }
}

