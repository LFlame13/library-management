package com.example.library_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryBookDTO {
    private Long id;
    @NotNull(message = "Серийный номер обязателен")
    @Min(value = 100000, message = "Серийный номер должен содержать минимум 6 цифр")
    private Long serialNumber;
    private String status;
    @NotNull(message = "Информация о книге обязательна")
    private Long bookInfoId;
}
