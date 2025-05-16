package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO для книги")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryBookDTO {
    @Schema(description = "Уникальный идентификатор книги", example = "1", hidden = true)
    private Long id;

    @NotNull(message = "Серийный номер обязателен")
    @Min(value = 100000, message = "Серийный номер должен содержать минимум 6 цифр")
    @Schema(description = "Уникальный серийный номер книги", example = "666666")
    private Long serialNumber;

    @Schema(description = "Статуст книг доступна или нет", example = "AVAILABLE")
    private String status;


    @NotNull(message = "Информация о книге обязательна")
    @Schema(description = "Id на информацию по книге", example = "1")
    private Long bookInfoId;
}
