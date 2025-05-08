package com.example.library_management.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoDTO {
    private Long id;
    @NotBlank(message = "Название книги обязательно")
    private String title;
    @NotBlank(message = "Автор обязателен")
    private String author;
    @NotNull(message = "Категория обязательна")
    private Long categoryId;
}
