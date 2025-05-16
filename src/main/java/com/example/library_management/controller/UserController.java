package com.example.library_management.controller;

import com.example.library_management.dto.LoginDTO;
import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.dto.UserDTO;
import com.example.library_management.mapper.UserMapper;
import com.example.library_management.model.User;
import com.example.library_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Пользователи", description = "Методы для управления пользователями")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя с указанной ролью (например, ADMIN или USER)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о пользователе",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            )
            @Valid @RequestBody UserDTO userDTO
    ) {
        User user = userMapper.toEntity(userDTO);
        userService.register(user, userDTO.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Позволяет пользователю войти в систему и получить JWT токен",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход"),
                    @ApiResponse(responseCode = "401", description = "Неверные данные", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)

            }
    )

    //Логин
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDTO.class))
            )
            @Valid @RequestBody LoginDTO loginDTO
    ) {
        String token = userService.login(loginDTO);
        return ResponseEntity.ok(Map.of("token", token));
    }
    @Operation(
            summary = "Частичное обновление пользователя",
            description = "Доступно только администратору. Позволяет обновить часть данных пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)

            }
    )

    // Обновление
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateUserPartially(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto
    ) {
        userService.updatePartUser(id, dto);
        log.info("Пользователь с ID {} успешно обновлен", id);
        return ResponseEntity.ok("Данные пользователя обновлены");
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Удаляет пользователя по ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)

            }
    )

    // Удаление
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        log.info("Пользователь с id={} удален", id);
        return ResponseEntity.ok("Пользователь удален");
    }
}

