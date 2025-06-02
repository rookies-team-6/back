package com.boanni_back.project.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminProgressServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getProgress_validUserId_shouldRenderProgressView() throws Exception {
        mockMvc.perform(get("/admin/users/progress/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/progress/detail"))
                .andExpect(model().attributeExists("progress"))
                .andDo(print());
    }

    @Test
    void getProgress_invalidUserId_shouldReturnErrorViewOr4xx() throws Exception {
        mockMvc.perform(get("/admin/users/progress/999"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}
