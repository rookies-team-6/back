package com.boanni_back.project.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsers_rendersUserListView() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"))
                .andExpect(model().attributeExists("users"))
                .andDo(print());
    }

    @Test
    void getUserByEmail_rendersUserDetailView() throws Exception {
        mockMvc.perform(get("/admin/users/email/trainee1@boanni.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/detail"))
                .andExpect(model().attributeExists("user"))
                .andDo(print());
    }

    @Test
    void getUsersByType_rendersFilteredListView() throws Exception {
        mockMvc.perform(get("/admin/users/type/employee"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list_by_type"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("type"))
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void deleteUserById_redirectsToUserList() throws Exception {
        mockMvc.perform(post("/admin/users/4/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void promoteUserToAdmin_redirectsToUserList() throws Exception {
        mockMvc.perform(patch("/admin/users/2/role"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andDo(print());
    }
}