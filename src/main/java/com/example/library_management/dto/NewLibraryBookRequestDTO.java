package com.example.library_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewLibraryBookRequestDTO {
    @NotBlank(message = "Название книги обязательно")
    private String title;

    @NotBlank(message = "Автор обязателен")
    private String author;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    @NotNull(message = "Серийный номер обязателен")
    @Min(value = 100000, message = "Серийный номер должен содержать минимум 6 цифр")
    private Long serialNumber;
}
