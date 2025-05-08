package com.example.library_management.mapper;

import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.model.LibraryBook;
import org.springframework.stereotype.Component;

@Component
public class LibraryBookMapper {
    public LibraryBookDTO toDTO(LibraryBook book) {
        return new LibraryBookDTO(
                book.getId(),
                book.getSerialNumber(),
                book.getStatus().name(),
                book.getBookInfo().getId()
        );
    }
}
