package com.example.library_management.controller;

import com.example.library_management.dto.LoginDTO;
import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.dto.UserDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.mapper.UserMapper;
import com.example.library_management.model.User;
import com.example.library_management.security.JwtUtil;
import com.example.library_management.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
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
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void registerUser_validInput() throws Exception {
        UserDTO dto = new UserDTO(null, "Viktor13", "pass12345", "USER");
        User user = new User();

        when(userMapper.toEntity(dto)).thenReturn(user);
        Mockito.doNothing().when(userService).register(any(User.class), eq("USER"));


        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
        .andExpect(content().string("Пользователь успешно зарегистрирован"));
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
    void loginUser_validCredentials_returnsToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO("user", "pass12345");
        String token = "jwt-token";

        when(userService.login(any(LoginDTO.class))).thenReturn(token);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void loginUser_invalidCredentials_returnsUnauthorized() throws Exception {
        LoginDTO loginDTO = new LoginDTO("user", "wrongpass");

        when(userService.login(any(LoginDTO.class))).thenThrow(new BadCredentialsException("Неверные учетные данные"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
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
    @WithMockUser(roles = "ADMIN")
    void updateUserPartially_withEmptyFields_returnsBadRequest() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO(" ", " ", "");

        mockMvc.perform(patch("/api/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
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
    @WithMockUser(roles = "ADMIN")
    void deleteUser_notFound_returnsNotFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Пользователь не найден"))
                .when(userService).deleteById(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_withoutAdminRole_returnsForbidden() throws Exception {
        UserDTO dto = new UserDTO(null, "user", "pass12345", "USER");

        String validJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLQn9C-0LvRjNC30L7QstCw0YLQtdC70YwxIiwicm9sZXMiOlsiVVNFUiJdLCJpYXQiOjE3NDcwNTU0NDMsImV4cCI6MTc0NzY1NTQ0M30.CZHVGfypEWB2o3qWyJv0IlUGtaoneix3ZZYQsl7gIU8";

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + validJwtToken))
                .andExpect(status().isForbidden());
    }
}