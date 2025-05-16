package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO для обновления книги")
@Getter
@Setter
public class UpdateBookInfoDTO {

    @NotNull(message = "ID книги обязателен")
    @Schema(description = "Уникальный идентификатор книги", example = "1")
    private Long bookInfoId;

    @NotBlank(message = "Название книги обязательно")
    @Schema(description = "Название для книги", example = "Босния")
    private String title;

    @NotBlank(message = "Автор обязателен")
    @Schema(description = "Автор книги", example = "Виктор Викторов")
    private String author;

    @NotNull(message = "Категория обязательна")
    @Schema(description = "Категория книги", example = "1")
    private Long categoryId;
}
