package com.example.library_management.controller;

import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.dto.UserDTO;
import com.example.library_management.mapper.UserMapper;
import com.example.library_management.model.User;
import com.example.library_management.service.UserService;
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
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        String token = userService.register(user, userDTO.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token));
    }

    // Обновление
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateUserPartially(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        userService.updatePartUser(id, dto);
        log.info("Пользователь с ID {} успешно обновлен", id);
        return ResponseEntity.ok("Данные пользователя обновлены");
    }

    // Удаление
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        log.info("Пользователь с id={} удален", id);
        return ResponseEntity.ok("Пользователь удален");
    }
}

