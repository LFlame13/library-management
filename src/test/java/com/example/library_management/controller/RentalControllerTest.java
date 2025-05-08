package com.example.library_management.controller;

import com.example.library_management.dto.RentalDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.model.Rental;
import com.example.library_management.model.User;
import com.example.library_management.service.RentalService;
import com.example.library_management.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("rental-test")
@SpringBootTest
@ContextConfiguration(classes = {Main.class, TestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalMapper rentalMapper;

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void rentBook_validUserAndBookId_returnsSuccessMessage() throws Exception {
        Long bookId = 89L;
        String username = "user1";

        User user = new User();
        user.setUsername(username);

        when(userService.findByUsername(username)).thenReturn(user);

        doNothing().when(rentalService).rentBook(user, bookId);

        mockMvc.perform(post("/api/rentals/rent/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string("Книга успешно арендована"));
    }


    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void returnBook_validBookId_returnsSuccessMessage() throws Exception {
        Long bookId = 1L;
        String username = "user1";
        User user = new User();
        user.setUsername(username);

        when(userService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(post("/api/rentals/return/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string("Книга успешно возвращена"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRentals_returnsRentalDTOList() throws Exception {
        Rental rental = new Rental();
        RentalDTO dto = new RentalDTO(1L, 2L, 3L, LocalDateTime.now(), LocalDateTime.now().plusDays(7), null);

        when(rentalService.getAllRentals()).thenReturn(List.of(rental));
        when(rentalMapper.toDTO(any(Rental.class))).thenReturn(dto);

        mockMvc.perform(get("/api/rentals/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalsByUser_userFound_returnsRentals() throws Exception {
        Long userId = 10L;
        Rental rental = new Rental();
        RentalDTO dto = new RentalDTO(1L, userId, 3L, LocalDateTime.now(), LocalDateTime.now().plusDays(7), null);

        when(rentalService.getRentalsByUser(userId)).thenReturn(List.of(rental));
        when(rentalMapper.toDTO(any(Rental.class))).thenReturn(dto);

        mockMvc.perform(get("/api/rentals/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalsByUser_userNotFound_returns404() throws Exception {
        Long userId = 999L;

        when(rentalService.getRentalsByUser(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rentals/user/{userId}", userId))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalsByBook_bookFound_returnsRentals() throws Exception {
        Long bookId = 5L;
        Rental rental = new Rental();
        RentalDTO dto = new RentalDTO(1L, 2L, bookId, LocalDateTime.now(), LocalDateTime.now().plusDays(7), null);

        when(rentalService.getRentalsByBook(bookId)).thenReturn(List.of(rental));
        when(rentalMapper.toDTO(any(Rental.class))).thenReturn(dto);

        mockMvc.perform(get("/api/rentals/book/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].libraryBookId").value(bookId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalsByBook_notFound_returns404() throws Exception {
        Long bookId = 777L;

        when(rentalService.getRentalsByBook(bookId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rentals/book/{bookId}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOverdueRentals_returnsList() throws Exception {
        RentalDTO dto = new RentalDTO(1L, 2L, 3L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), null);

        when(rentalService.getOverdueRentals()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rentals/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()));
    }
}
