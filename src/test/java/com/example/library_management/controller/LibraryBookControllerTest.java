package com.example.library_management.controller;

import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.dto.NewLibraryBookRequestDTO;
import com.example.library_management.dto.UpdateBookInfoDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.service.LibraryBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("library-book-test")
@SpringBootTest
@ContextConfiguration(classes = {Main.class, TestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LibraryBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LibraryBookService libraryBookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBooks_returnsBooks() throws Exception {
        LibraryBookDTO book1 = new LibraryBookDTO(1L, 123456L, "AVAILABLE", 1L);
        LibraryBookDTO book2 = new LibraryBookDTO(2L, 654321L, "RENTED", 2L);

        when(libraryBookService.getAllBooks()).thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].serialNumber").value(654321L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllBooks_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addNewBook_returnsSuccessMessage() throws Exception {
        NewLibraryBookRequestDTO newBookRequest = new NewLibraryBookRequestDTO();
        newBookRequest.setTitle("New Book");
        newBookRequest.setAuthor("Author Name");
        newBookRequest.setCategoryId(1L);
        newBookRequest.setSerialNumber(123456L);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Новая книга успешно добавлена"));
    }


    @Test
    @WithMockUser(roles = "USER")
    void addNewBook_forbiddenForUser() throws Exception {
        NewLibraryBookRequestDTO newBookRequest = new NewLibraryBookRequestDTO();
        newBookRequest.setTitle("New Book");
        newBookRequest.setAuthor("Author Name");
        newBookRequest.setCategoryId(1L);
        newBookRequest.setSerialNumber(123456L);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookById_returnsBook() throws Exception {
        LibraryBookDTO book = new LibraryBookDTO(1L, 123456L, "AVAILABLE", 1L);
        when(libraryBookService.getBookDTOById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.serialNumber").value(123456L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookById_notFound() throws Exception {
        when(libraryBookService.getBookDTOById(99L))
                .thenThrow(new EntityNotFoundException("Книга с ID 99 не найдена"));

        mockMvc.perform(get("/api/books/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Книга с ID 99 не найдена"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBookInfo_returnsSuccessMessage() throws Exception {
        UpdateBookInfoDTO updateInfo = new UpdateBookInfoDTO();
        updateInfo.setTitle("Updated Book");
        updateInfo.setAuthor("Updated Author");
        updateInfo.setCategoryId(1L);
        updateInfo.setBookInfoId(1l);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(content().string("Информация о книге успешно обновлена"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBookInfo_bookNotFound() throws Exception {
        UpdateBookInfoDTO updateInfo = new UpdateBookInfoDTO();
        updateInfo.setTitle("Updated");
        updateInfo.setAuthor("Author");
        updateInfo.setCategoryId(1L);
        updateInfo.setBookInfoId(999L);

        doThrow(new EntityNotFoundException("Книга с ID 999 не найдена"))
                .when(libraryBookService).updateBookInfo(eq(999L), any(UpdateBookInfoDTO.class));

        mockMvc.perform(put("/api/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Книга с ID 999 не найдена"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_returnsSuccessMessage() throws Exception {
        doNothing().when(libraryBookService).deleteLibraryBook(1L);

        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Книга и информация по ней успешно удалена"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_bookRented_throwsError() throws Exception {
        doThrow(new IllegalStateException("Нельзя удалить книгу, она сейчас в аренде"))
                .when(libraryBookService).deleteLibraryBook(1L);

        mockMvc.perform(delete("/api/books/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Нельзя удалить книгу, она сейчас в аренде"));
    }
}
