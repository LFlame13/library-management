package com.example.library_management.controller;

import com.example.library_management.dto.AuditLogDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.mapper.AuditMapper;
import com.example.library_management.model.AuditLog;
import com.example.library_management.model.User;
import com.example.library_management.service.AuditService;
import com.example.library_management.service.UserService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("audit-test")
@SpringBootTest
@ContextConfiguration(classes = {Main.class, TestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditMapper auditMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLogs_shouldReturnListOfLogs() throws Exception {
        AuditLog log = new AuditLog();
        log.setId(1L);
        AuditLogDTO dto = new AuditLogDTO(1L, 2L, "ACTION", 3L);

        when(auditService.getAllLogs()).thenReturn(List.of(log));
        when(auditMapper.toDTO(log)).thenReturn(dto);

        mockMvc.perform(get("/api/audit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].action").value("ACTION"))
                .andExpect(jsonPath("$[0].libraryBookId").value(3L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLogsByBookId_shouldReturnLogs() throws Exception {
        Long bookId = 5L;
        AuditLog log = new AuditLog();
        log.setId(10L);
        AuditLogDTO dto = new AuditLogDTO(10L, 2L, "READ", bookId);

        when(auditService.getLogsByBookId(bookId)).thenReturn(List.of(log));
        when(auditMapper.toDTO(log)).thenReturn(dto);

        mockMvc.perform(get("/api/audit/book/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].userId").value(2L))
                .andExpect(jsonPath("$[0].action").value("READ"))
                .andExpect(jsonPath("$[0].libraryBookId").value(5L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLogsByBookId_shouldReturnNotFound() throws Exception {
        Long bookId = 99L;
        when(auditService.getLogsByBookId(bookId)).thenReturn(List.of());

        mockMvc.perform(get("/api/audit/book/{bookId}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Логов для книги с ID " + bookId + " пока нет"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLogsByUserId_shouldReturnLogs() throws Exception {
        Long userId = 7L;
        User user = new User();
        user.setId(userId);

        AuditLog log = new AuditLog();
        log.setId(20L);
        AuditLogDTO dto = new AuditLogDTO(20L, userId, "BORROW", 9L);

        when(userService.getUserById(userId)).thenReturn(user);
        when(auditService.getLogsByUser(user)).thenReturn(List.of(log));
        when(auditMapper.toDTO(log)).thenReturn(dto);

        mockMvc.perform(get("/api/audit/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].action").value("BORROW"))
                .andExpect(jsonPath("$[0].libraryBookId").value(9L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLogsByUserId_shouldReturnNotFound() throws Exception {
        Long userId = 777L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(auditService.getLogsByUser(user)).thenReturn(List.of());

        mockMvc.perform(get("/api/audit/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Логов для пользователя с ID " + userId + " пока нет"));
    }
}