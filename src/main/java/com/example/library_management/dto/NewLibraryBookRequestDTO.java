package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO для добавления новой книги")
@Getter
@Setter
public class NewLibraryBookRequestDTO {
    @NotBlank(message = "Название книги обязательно")
    @Schema(description = "Название для книги", example = "Черногория")
    private String title;

    @NotBlank(message = "Автор обязателен")
    @Schema(description = "Автор книги", example = "Борис Борисов")
    private String author;

    @NotNull(message = "Категория обязательна")
    @Schema(description = "Категория книги", example = "1")
    private Long categoryId;

    @NotNull(message = "Серийный номер обязателен")
    @Min(value = 100000, message = "Серийный номер должен содержать минимум 6 цифр")
    @Schema(description = "Уникальный серийный номер книги", example = "666661")
    private Long serialNumber;
}
