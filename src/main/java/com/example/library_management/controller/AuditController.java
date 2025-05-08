package com.example.library_management.controller;

import com.example.library_management.dto.AuditLogDTO;
import com.example.library_management.mapper.AuditMapper;
import com.example.library_management.model.AuditLog;
import com.example.library_management.model.User;
import com.example.library_management.service.AuditService;
import com.example.library_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final UserService userService;
    private final AuditMapper auditMapper;

    // Получить все логи
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogDTO>> getAllLogs() {
        List<AuditLogDTO> dtos = auditService.getAllLogs().stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Получить логи по ID книги
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/book/{bookId}")
        public ResponseEntity<?> getLogsByBookId(@PathVariable Long bookId) {
            List<AuditLog> logs = auditService.getLogsByBookId(bookId);

            if (logs.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Логов для книги с ID " + bookId + " пока нет");
            }

            List<AuditLogDTO> dtos = logs.stream()
                    .map(auditMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }

    // Получить логи по ID пользователя
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsByUserId(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<AuditLog> logs = auditService.getLogsByUser(user);

        if (logs.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Логов для пользователя с ID " + userId + " пока нет");
        }

        List<AuditLogDTO> dtos = logs.stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
