package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminDeadlineServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDeadline_NotExists() throws Exception {
        mockMvc.perform(get("/admin/users/deadline/1"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void setDeadline_Success() throws Exception {
        AdminDeadlineDto dto = new AdminDeadlineDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        mockMvc.perform(patch("/admin/users/deadline/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/admin/users/deadline/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate", is(dto.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(dto.getEndDate().toString())))
                .andDo(print());
    }

    @Test
    void setDeadline_StartDateError() throws Exception {
        AdminDeadlineDto dto = new AdminDeadlineDto(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5)
        );

        mockMvc.perform(patch("/admin/users/deadline/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void setDeadline_EndDateError() throws Exception {
        AdminDeadlineDto dto = new AdminDeadlineDto(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2)
        );

        mockMvc.perform(patch("/admin/users/deadline/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}

