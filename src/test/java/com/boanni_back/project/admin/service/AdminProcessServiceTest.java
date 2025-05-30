package com.boanni_back.project.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql")
public class AdminProcessServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getProgress_ValidUserId() throws Exception {
        long userId = 1L;

        ResultActions result = mockMvc.perform(get("/admin/users/progress/" + userId));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("수강생1")))
                .andExpect(jsonPath("$.progress", is("20%")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getProgress_NotExistsUserId() throws Exception {
        long userId = 999L;
        ResultActions result = mockMvc.perform(get("/admin/users/progress/" + userId));

        result.andExpect(status().is4xxClientError())
                .andExpect(status().isOk())
                .andDo(print());
    }
}
