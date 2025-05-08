package com.example.library_management.controller;

import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.dto.NewLibraryBookRequestDTO;
import com.example.library_management.dto.UpdateBookInfoDTO;
import com.example.library_management.service.LibraryBookService;
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
public class LibraryBookController {

    private final LibraryBookService libraryBookService;

    // Получить все книги
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<LibraryBookDTO>> getAllBooks() {
        List<LibraryBookDTO> books = libraryBookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

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

    // Получить книгу по ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<LibraryBookDTO> getBookById(@PathVariable Long id) {
        LibraryBookDTO dto = libraryBookService.getBookDTOById(id);
        return ResponseEntity.ok(dto);
    }

    // Удалить книгу по ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        libraryBookService.deleteLibraryBook(id);
        log.info("Удалена книга с ID {}", id);
        return ResponseEntity.ok("Книга и информация по ней успешно удалена");
    }

    // Обновить книгу по ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateBookInfo(@PathVariable Long id, @Valid @RequestBody UpdateBookInfoDTO updateBookInfoDTO) {
        libraryBookService.updateBookInfo(id, updateBookInfoDTO);
        log.info("Информация о книге с ID {} обновлена", id);
        return ResponseEntity.ok("Информация о книге успешно обновлена");
    }
}
