package com.example.library_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO для обновления пользователя")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 6, message = "Имя должно содержать минимум 6 символов")
    @Schema(description = "Имя пользователя (логин)", example = "Victorovich")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Schema(description = "Пароль пользователя (не менее 6 символов)", example = "SuperPAASSWWORD")
    private String password;

    @NotBlank(message = "Роль обязательна")
    @Schema(description = "Роль пользователя (не менее 6 символов)", example = "ADMIN")
    private String role;
}
