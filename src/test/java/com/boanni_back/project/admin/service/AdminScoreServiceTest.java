package com.boanni_back.project.admin.service;

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
class AdminScoreServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUserScores_shouldRenderScoreListView() throws Exception {
        mockMvc.perform(get("/admin/users/scores"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scores/list"))
                .andExpect(model().attributeExists("scores"))
                .andDo(print());
    }

    @Test
    void getUserScoreById_shouldRenderScoreDetailView() throws Exception {
        mockMvc.perform(get("/admin/users/scores/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scores/detail"))
                .andExpect(model().attributeExists("score"))
                .andDo(print());
    }

    @Test
    void getScoresSortedDesc_shouldRenderSortedScoreView() throws Exception {
        mockMvc.perform(get("/admin/users/scores/sorted"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scores/sorted"))
                .andExpect(model().attributeExists("scores"))
                .andDo(print());
    }
}
