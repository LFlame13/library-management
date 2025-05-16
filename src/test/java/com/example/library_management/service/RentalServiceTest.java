package com.example.library_management.service;

import com.example.library_management.dao.LibraryBookDAO;
import com.example.library_management.dao.RentalDAO;
import com.example.library_management.dto.RentalDTO;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.model.LibraryBook;
import com.example.library_management.model.Rental;
import com.example.library_management.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalDAO rentalDAO;
    @Mock
    private LibraryBookDAO libraryBookDAO;
    @Mock
    private AuditService auditService;
    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalService rentalService;

    private User user;
    private LibraryBook book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");

        book = new LibraryBook();
        book.setId(10L);
    }

    @Test
    void rentBook_success() {
        book.setStatus(LibraryBook.BookStatus.AVAILABLE);

        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));

        rentalService.rentBook(user, 10L);

        assertEquals(LibraryBook.BookStatus.RENTED, book.getStatus());
        verify(rentalDAO).save(any(Rental.class));
        verify(libraryBookDAO).update(book);
        verify(auditService).logAction(user, book, "BOOK_RENTED");
    }

    @Test
    void rentBook_whenBookNotAvailable_throwsException() {
        book.setStatus(LibraryBook.BookStatus.RENTED);
        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> rentalService.rentBook(user, 10L));

        assertEquals("Книга недоступна для аренды", ex.getMessage());
        verify(rentalDAO, never()).save(any());
    }

    @Test
    void rentBook_whenBookNotFound_throwsException() {
        when(libraryBookDAO.findById(10L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> rentalService.rentBook(user, 10L));

        assertEquals("Книга не найдена", ex.getMessage());
    }

    @Test
    void returnBook_success() {
        book.setStatus(LibraryBook.BookStatus.RENTED);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setLibraryBook(book);
        rental.setReturnedAt(null);

        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));
        when(rentalDAO.findByBookId(10L)).thenReturn(List.of(rental));

        rentalService.returnBook(10L, user);

        assertEquals(LibraryBook.BookStatus.AVAILABLE, book.getStatus());
        assertNotNull(rental.getReturnedAt());
        verify(libraryBookDAO).update(book);
        verify(rentalDAO).update(rental);
        verify(auditService).logAction(user, book, "BOOK_RETURNED");
    }

    @Test
    void returnBook_whenNoRentals_throwsException() {
        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));
        when(rentalDAO.findByBookId(10L)).thenReturn(Collections.emptyList());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> rentalService.returnBook(10L, user));

        assertEquals("Книга не арендована", ex.getMessage());
    }

    @Test
    void returnBook_wrongUser_throwsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("other");

        Rental rental = new Rental();
        rental.setUser(anotherUser);
        rental.setLibraryBook(book);
        rental.setReturnedAt(null);

        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));
        when(rentalDAO.findByBookId(10L)).thenReturn(List.of(rental));

        SecurityException ex = assertThrows(SecurityException.class,
                () -> rentalService.returnBook(10L, user));

        assertEquals("Вы не можете вернуть чужую книгу", ex.getMessage());
    }

    @Test
    void returnBook_whenAlreadyReturned_throwsException() {
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setLibraryBook(book);
        rental.setReturnedAt(LocalDateTime.now());

        when(libraryBookDAO.findById(10L)).thenReturn(Optional.of(book));
        when(rentalDAO.findByBookId(10L)).thenReturn(List.of(rental));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> rentalService.returnBook(10L, user));

        assertEquals("Книга уже возвращена", ex.getMessage());
    }

    @Test
    void getOverdueRentals_returnsOnlyOverdue() {
        Long userId = 1L;
        Rental rental = new Rental();
        rental.setUser(new User());
        rental.getUser().setId(userId);
        rental.setReturnedAt(null);
        rental.setDueDate(LocalDateTime.now().minusDays(1)); // просрочено

        RentalDTO dto = new RentalDTO();
        when(rentalDAO.findOverdueByUserId(userId)).thenReturn(List.of(rental));
        when(rentalMapper.toDTO(rental)).thenReturn(dto);

        List<RentalDTO> result = rentalService.getOverdueRentalsByUser(userId);

        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void getRentalsByUser_returnsCorrectList() {
        List<Rental> rentals = List.of(new Rental());
        when(rentalDAO.findByUserId(1L)).thenReturn(rentals);

        List<Rental> result = rentalService.getRentalsByUser(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getRentalsByBook_returnsCorrectList() {
        List<Rental> rentals = List.of(new Rental());
        when(rentalDAO.findByBookId(10L)).thenReturn(rentals);

        List<Rental> result = rentalService.getRentalsByBook(10L);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRentals_returnsAll() {
        List<Rental> rentals = List.of(new Rental(), new Rental());
        when(rentalDAO.findAll()).thenReturn(rentals);

        List<Rental> result = rentalService.getAllRentals();
        assertEquals(2, result.size());
    }
}
