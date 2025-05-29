package com.boanni_back.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql")
class AdminMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsers_returnsUserList() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(6)))
                .andDo(print());
    }

    @Test
    void getUserByEmail_returnsSingleUser() throws Exception {
        mockMvc.perform(get("/admin/users/emp1@boanni.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("사원1")))
                .andDo(print());
    }

    @Test
    void getUsersByType_returnsFilteredUsers() throws Exception {
        mockMvc.perform(get("/admin/users/type/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].employee_type", is("TRAINEE")))
                .andDo(print());
    }

    @Test
    void deleteUserById_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void promoteUserToAdmin_returnsOk() throws Exception {
        mockMvc.perform(patch("/admin/users/2/role"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}