package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO для логов аудита")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    @Schema(description = "Уникальный идентификатор категории", example = "1", hidden = true)
    private Long id;

    @Schema(description = "ID пользователя, совершившего действие", example = "1")
    private Long userId;

    @Schema(description = "Описание действия", example = "BOOK_RENTED")
    private String action;

    @Schema(description = "ID книги, с которой связано действие", example = "1")
    private Long libraryBookId;
}
