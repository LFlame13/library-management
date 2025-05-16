package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "DTO аренды книги")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    @Schema(description = "Уникальный идентификатор аренды", example = "1")
    private Long id;

    @NotNull(message = "ID книги обязательно")
    @Schema(description = "ID пользователя, арендовавшего книгу", example = "5")
    private Long userId;

    @Schema(description = "ID книги", example = "12")
    private Long libraryBookId;

    @Schema(description = "Дата начала аренды", example = "2025-05-01")
    private LocalDateTime rentedAt;


    @NotNull(message = "Срок возврата обязателен")
    @Schema(description = "Дата окончания аренды", example = "2025-05-8")
    private LocalDateTime dueDate;

    @Schema(description = "Флаг, указывающий, возвращена ли книга", example = "false")
    private LocalDateTime returnedAt;
}
