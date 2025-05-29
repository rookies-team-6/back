package com.boanni_back.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql")
class AdminMvcScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllUserScores() throws Exception {
        mockMvc.perform(get("/admin/users/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].username", notNullValue()))
                .andExpect(jsonPath("$[0].score", greaterThanOrEqualTo(0)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldGetUserScoreById() throws Exception {
        mockMvc.perform(get("/admin/users/scores/{id}", 1L)) // test-data.sql 기준
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", isA(Integer.class)))
                .andExpect(jsonPath("$.username", notNullValue()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldGetScoresSortedDesc() throws Exception {
        mockMvc.perform(get("/admin/users/scores/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$[0].username", notNullValue()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
