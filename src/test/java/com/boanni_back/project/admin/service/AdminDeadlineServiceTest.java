package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @WithMockUser(roles = "ADMIN")
    @Test
    void getDeadline_Exists() throws Exception {
        mockMvc.perform(get("/admin/users/deadline/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/deadline/detail"))
                .andExpect(model().attributeExists("deadlineDto"))
                .andDo(print());
    }


    @Test
    void getDeadline_NotExists() throws Exception {
        mockMvc.perform(get("/admin/users/deadline/9999"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    //마감일 수정 폼 페이지가 잘 렌더링되는지 확인
    @WithMockUser(roles = "ADMIN")
    @Test
    void getDeadlineEditForm_Exists() throws Exception {
        mockMvc.perform(get("/admin/users/deadline/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/deadline/edit"))
                .andExpect(model().attributeExists("deadlineDto"))
                .andDo(print());
    }

    //마감일 수정 성공
    @WithMockUser(roles = "ADMIN")
    @Test
    void setDeadline_Success() throws Exception {
        mockMvc.perform(patch("/admin/users/deadline/1")
                        .param("newDeadline", LocalDate.now().plusDays(15).toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/deadline/1"))
                .andDo(print());
    }


    //오늘 이전 날짜로 수정 시도했을 시
    @WithMockUser(roles = "ADMIN")
    @Test
    void setDeadline_PastDateError() throws Exception {
        mockMvc.perform(patch("/admin/users/deadline/1")
                        .param("newDeadline", LocalDate.now().minusDays(1).toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())  // 예외 후 redirect
                .andExpect(redirectedUrl("/admin/users/deadline/1"))
                .andDo(print());
    }

    //변경 전 마감일과 동일한 날짜로 수정했을 시
    @WithMockUser(roles = "ADMIN")
    @Test
    void setDeadline_SameDateError() throws Exception {
        LocalDate today = LocalDate.now().plusDays(15);

        // 마감일 동일한 날짜로 재요청
        mockMvc.perform(patch("/admin/users/deadline/1")
                        .param("newDeadline", today.toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/deadline/1"))
                .andDo(print());
    }

    //마감일 지난 회원 리스트 조회 테스트
    @WithMockUser(roles = "ADMIN")
    @Test
    void getExpiredUsersList() throws Exception {
        mockMvc.perform(get("/admin/users/deadline/expired"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("expiredUsers"))
                .andExpect(view().name("admin/deadline/expired"))
                .andDo(print());
    }
}