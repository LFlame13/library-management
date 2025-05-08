package com.example.library_management.controller;

import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.dto.UserDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.mapper.UserMapper;
import com.example.library_management.model.User;
import com.example.library_management.security.JwtUtil;
import com.example.library_management.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ContextConfiguration(classes = {Main.class, TestConfig.class})
@AutoConfigureMockMvc
@Import({JwtUtil.class, ObjectMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("mockUserMapper")
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_validInput_returnsToken() throws Exception {
        UserDTO dto = new UserDTO(null, "user", "pass12345", "USER");
        User user = new User();
        String token = "jwt-token";

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userService.register(any(), eq("USER"))).thenReturn(token);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void registerUser_invalidInput_returnsBadRequest() throws Exception {
        UserDTO dto = new UserDTO(null, "", "", "USER");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserPartially_adminCanUpdate() throws Exception {
        UpdateUserDTO updateDTO = new UpdateUserDTO("newName", "pass12345", "ADMIN");

        mockMvc.perform(patch("/api/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Данные пользователя обновлены"));

        Mockito.verify(userService).updatePartUser(eq(1L), refEq(updateDTO));
    }

    @Test
    void updateUserPartially_unauthorized_returnsUnauthorized() throws Exception {
        UpdateUserDTO updateDTO = new UpdateUserDTO("name", "pass12345", "USER");

        mockMvc.perform(patch("/api/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_validAdminRequest_returnsOk() throws Exception {
        Long userId = 1L;

        Mockito.doNothing().when(userService).deleteById(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь удален"));

        Mockito.verify(userService).deleteById(userId);
    }

    @Test
    void deleteUser_withoutAdminRole_returnsForbidden() throws Exception {
        UserDTO dto = new UserDTO(null, "user", "pass12345", "USER");

        String validJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLQktC40LrRgtC-0YAg0JDQvdC00YDQtdC40YciLCJyb2xlcyI6WyJVU0VSIl0sImlhdCI6MTc0NjM5MDE5MSwiZXhwIjoxNzQ2OTkwMTkxfQ.7vpzOwLhj5QU-x_-fvnIeB5QOoEgi65QMADBO-Gj6vM";

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + validJwtToken))
                .andExpect(status().isForbidden());
    }
}