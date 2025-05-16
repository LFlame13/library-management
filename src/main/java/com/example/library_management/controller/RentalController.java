package com.example.library_management.controller;

import com.example.library_management.dto.RentalDTO;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.model.Rental;
import com.example.library_management.model.User;
import com.example.library_management.service.RentalService;
import com.example.library_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Validated
@Tag(name = "Аренда", description = "Методы для управления арендой")
public class RentalController {

    private final RentalService rentalService;
    private final UserService userService;
    private final RentalMapper rentalMapper;

    @Operation(
            summary = "Арендовать книгу",
            description = "Позволяет пользователю арендовать книгу по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно арендована"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Арендовать книгу
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/rent/{bookId}")
    public ResponseEntity<String> rentBook(
            @Parameter(description = "ID книги для аренды", example = "1")
            @PathVariable Long bookId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        rentalService.rentBook(user, bookId);

        log.info("Пользователь '{}' арендовал книгу ID '{}'", username, bookId);
        return ResponseEntity.ok("Книга успешно арендована");
    }

    @Operation(
            summary = "Вернуть книгу",
            description = "Позволяет пользователю вернуть книгу по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно возвращена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Вернуть книгу
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(
            @Parameter(description = "ID книги для возврата", example = "1")
            @PathVariable Long bookId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        rentalService.returnBook(bookId, currentUser);
        return ResponseEntity.ok("Книга успешно возвращена");
    }

    @Operation(
            summary = "Получить все аренды",
            description = "Возвращает список всех аренд. Доступно только администратору.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список аренд успешно получен",
                            content = @Content(schema = @Schema(implementation = RentalDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить все аренды
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        List<RentalDTO> rentalDTOs = rentalService.getAllRentals().stream()
                .map(rentalMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Получено {} аренд из базы данных", rentalDTOs.size());
        return ResponseEntity.ok(rentalDTOs);
    }

    @Operation(
            summary = "Получить аренды пользователя",
            description = "Возвращает список аренд по ID пользователя. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аренды пользователя получены",
                            content = @Content(schema = @Schema(implementation = RentalDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Аренды не найдены", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRentalsByUser(
            @Parameter(description = "ID пользователя", example = "6")
            @PathVariable Long userId
    ) {
        List<Rental> rentals = rentalService.getRentalsByUser(userId);

        if (rentals.isEmpty()) {
            throw new EntityNotFoundException("Аренды для пользователя с ID " + userId + " не найдены");
        }

        log.info("Получены аренды для пользователя с ID '{}'. Найдено {} аренд.", userId, rentals.size());

        List<RentalDTO> rentalDTOs = rentals.stream()
                .map(rentalMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rentalDTOs);
    }

    @Operation(
            summary = "Получить аренды книги",
            description = "Возвращает список аренд по ID книги. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аренды книги получены",
                            content = @Content(schema = @Schema(implementation = RentalDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Аренды не найдены", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить аренды книги
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getRentalsByBook(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long bookId
    ) {
        List<Rental> rentals = rentalService.getRentalsByBook(bookId);

        if (rentals.isEmpty()) {
            throw new EntityNotFoundException("Аренды для книги с ID " + bookId + " не найдены");
        }

        log.info("Получены аренды для книги с ID '{}'. Найдено {} аренд", bookId, rentals.size());

        List<RentalDTO> rentalDTOs = rentals.stream()
                .map(rentalMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rentalDTOs);
    }

    @Operation(
            summary = "Получить просроченные аренды по пользователю",
            description = "Возвращает список просроченных аренд для указанного пользователя. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Просроченные аренды успешно получены",
                            content = @Content(schema = @Schema(implementation = RentalDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )


    // Получить просроченные аренды по пользователю
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue/{userId}")
    public ResponseEntity<List<RentalDTO>> getOverdueRentalsByUser(
            @Parameter(description = "ID пользователя", example = "3")
            @PathVariable Long userId
    ) {
        List<RentalDTO> overdueRentals = rentalService.getOverdueRentalsByUser(userId);

        log.info("Получены просроченные аренды пользователя с ID '{}'. Найдено {} записей", userId, overdueRentals.size());
        return ResponseEntity.ok(overdueRentals);
    }
}
