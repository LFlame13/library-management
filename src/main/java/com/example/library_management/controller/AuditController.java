package com.example.library_management.controller;

import com.example.library_management.dto.AuditLogDTO;
import com.example.library_management.mapper.AuditMapper;
import com.example.library_management.model.AuditLog;
import com.example.library_management.model.User;
import com.example.library_management.service.AuditService;
import com.example.library_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Аудит", description = "Методы для получения логов книг")
public class AuditController {

    private final AuditService auditService;
    private final UserService userService;
    private final AuditMapper auditMapper;

    @Operation(
            summary = "Получить все логи",
            description = "Возвращает список всех логов аудита. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список логов успешно получен",
                            content = @Content(schema = @Schema(implementation = AuditLogDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить все логи
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogDTO>> getAllLogs() {
        List<AuditLogDTO> dtos = auditService.getAllLogs().stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "Получить логи по ID книги",
            description = "Возвращает список логов, связанных с конкретной книгой по её ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Логи найдены",
                            content = @Content(schema = @Schema(implementation = AuditLogDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Логи для книги не найдены", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить логи по ID книги
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getLogsByBookId(
            @Parameter(description = "ID книги", example = "1") @PathVariable Long bookId) {
        List<AuditLog> logs = auditService.getLogsByBookId(bookId);

        if (logs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Логов для книги с ID " + bookId + " пока нет");
        }

        List<AuditLogDTO> dtos = logs.stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "Получить логи по ID пользователя",
            description = "Возвращает список логов, связанных с конкретным пользователем по его ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Логи найдены",
                            content = @Content(schema = @Schema(implementation = AuditLogDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Логи для пользователя не найдены", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить логи по ID пользователя
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsByUserId(
            @Parameter(description = "ID пользователя", example = "1") @PathVariable Long userId
    ) {
        User user = userService.getUserById(userId);
        List<AuditLog> logs = auditService.getLogsByUser(user);

        if (logs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Логов для пользователя с ID " + userId + " пока нет");
        }

        List<AuditLogDTO> dtos = logs.stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
