package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO для логина")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Имя пользователя обязательно")
    @Schema(description = "Имя пользователя (логин)", example = "Vasilich")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Schema(description = "Пароль пользователя (не менее 6 символов)", example = "MyPassword123!")
    private String password;
}
