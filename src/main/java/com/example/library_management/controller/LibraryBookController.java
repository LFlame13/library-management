package com.example.library_management.controller;

import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.dto.NewLibraryBookRequestDTO;
import com.example.library_management.dto.UpdateBookInfoDTO;
import com.example.library_management.service.LibraryBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
@Tag(name = "Книги", description = "Методы для управления книгами")
public class LibraryBookController {

    private final LibraryBookService libraryBookService;

    @Operation(
            summary = "Получить все книги",
            description = "Возвращает список всех книг в библиотеке. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список книг успешно получен",
                            content = @Content(schema = @Schema(implementation = LibraryBookDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить все книги
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<LibraryBookDTO>> getAllBooks() {
        List<LibraryBookDTO> books = libraryBookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Добавить новую книгу",
            description = "Добавляет новую книгу в библиотеку. Только для администратора.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные новой книги",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewLibraryBookRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно добавлена"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Добавить новую книгу
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> addNewBook(@Valid @RequestBody NewLibraryBookRequestDTO newBookRequest) {
        libraryBookService.addFullBook(
                newBookRequest.getTitle(),
                newBookRequest.getAuthor(),
                newBookRequest.getCategoryId(),
                newBookRequest.getSerialNumber()
        );
        log.info("Добавлена новая книга: '{}' автора '{}'", newBookRequest.getTitle(), newBookRequest.getAuthor());
        return ResponseEntity.ok("Новая книга успешно добавлена");
    }

    @Operation(
            summary = "Получить книгу по ID",
            description = "Возвращает данные о книге по её ID. Доступно для пользователя и администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга найдена",
                            content = @Content(schema = @Schema(implementation = LibraryBookDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить книгу по ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<LibraryBookDTO> getBookById(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id
    ) {
        LibraryBookDTO dto = libraryBookService.getBookDTOById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Удалить книгу по ID",
            description = "Удаляет книгу и связанную информацию по её ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Книга успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Удалить книгу по ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBook(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id
    ) {
        libraryBookService.deleteLibraryBook(id);
        log.info("Удалена книга с ID {}", id);
        return ResponseEntity.ok("Книга и информация по ней успешно удалена");
    }

    @Operation(
            summary = "Обновить информацию о книге",
            description = "Обновляет информацию о книге по ID. Только для администратора.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новая информация о книге",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateBookInfoDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о книге успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Обновить книгу по ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateBookInfo(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookInfoDTO updateBookInfoDTO
    ) {
        libraryBookService.updateBookInfo(id, updateBookInfoDTO);
        log.info("Информация о книге с ID {} обновлена", id);
        return ResponseEntity.ok("Информация о книге успешно обновлена");
    }
}
