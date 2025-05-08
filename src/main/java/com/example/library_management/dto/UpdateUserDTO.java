package com.example.library_management.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String username;
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
    private String role;
}
