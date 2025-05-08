package com.example.library_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    private Long id;
    @NotNull(message = "ID книги обязательно")
    private Long userId;
    private Long libraryBookId;
    private LocalDateTime rentedAt;
    @NotNull(message = "Срок возврата обязателен")
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;
}
