package com.example.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookInfoDTO {

    @NotNull(message = "ID книги обязателен")
    private Long bookInfoId;
    @NotBlank(message = "Название книги обязательно")
    private String title;
    @NotBlank(message = "Автор обязателен")
    private String author;
    @NotNull(message = "Категория обязательна")
    private Long categoryId;
}
