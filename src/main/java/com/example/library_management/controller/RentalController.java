package com.example.library_management.controller;

import com.example.library_management.dto.RentalDTO;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.model.Rental;
import com.example.library_management.model.User;
import com.example.library_management.service.RentalService;
import com.example.library_management.service.UserService;
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
public class RentalController {

    private final RentalService rentalService;
    private final UserService userService;
    private final RentalMapper rentalMapper;

    // Арендовать книгу
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/rent/{bookId}")
    public ResponseEntity<String> rentBook(@PathVariable Long bookId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        rentalService.rentBook(user, bookId);

        log.info("Пользователь '{}' арендовал книгу ID '{}'", username, bookId);
        return ResponseEntity.ok("Книга успешно арендована");
    }

    // Вернуть книгу
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        rentalService.returnBook(bookId, currentUser);
        return ResponseEntity.ok("Книга успешно возвращена");
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRentalsByUser(@PathVariable Long userId) {
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

    // Получить аренды книги
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getRentalsByBook(@PathVariable Long bookId) {
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

    // Получить просроченные аренды
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<RentalDTO>> getOverdueRentals() {
        List<RentalDTO> overdueRentals = rentalService.getOverdueRentals();

        log.info("Получены просроченные аренды. Найдено {} просроченных записей", overdueRentals.size());

        return ResponseEntity.ok(overdueRentals);
    }
}
