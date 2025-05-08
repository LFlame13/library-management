package com.example.library_management.service;

import com.example.library_management.dao.AuditLogDAO;
import com.example.library_management.model.AuditLog;
import com.example.library_management.model.LibraryBook;
import com.example.library_management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    @Mock
    private AuditLogDAO auditLogDAO;

    @InjectMocks
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logAction_validInput_savesLog() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        LibraryBook book = new LibraryBook();
        book.setId(100L);

        String action = "взял книгу";

        auditService.logAction(user, book, action);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogDAO).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertEquals(user, savedLog.getUser());
        assertEquals(book, savedLog.getBook());
        assertEquals(action, savedLog.getAction());
    }

    @Test
    void logAction_nullUser_throwsException() {
        LibraryBook book = new LibraryBook();
        assertThrows(IllegalArgumentException.class, () ->
                auditService.logAction(null, book, "BOOK_RENTED"));
    }

    @Test
    void logAction_nullBook_throwsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () ->
                auditService.logAction(user, null, "BOOK_RENTED"));
    }

    @Test
    void logAction_blankAction_throwsException() {
        User user = new User();
        LibraryBook book = new LibraryBook();
        assertThrows(IllegalArgumentException.class, () ->
                auditService.logAction(user, book, "  "));
    }

    @Test
    void getAllLogs_returnsList() {
        List<AuditLog> logs = List.of(new AuditLog(), new AuditLog());
        when(auditLogDAO.findAll()).thenReturn(logs);

        List<AuditLog> result = auditService.getAllLogs();

        assertEquals(2, result.size());
        verify(auditLogDAO).findAll();
    }

    @Test
    void getLogsByBookId_returnsList() {
        Long bookId = 123L;
        when(auditLogDAO.findByBookId(bookId)).thenReturn(List.of(new AuditLog()));

        List<AuditLog> result = auditService.getLogsByBookId(bookId);

        assertEquals(1, result.size());
        verify(auditLogDAO).findByBookId(bookId);
    }

    @Test
    void getLogsByUser_returnsList() {
        User user = new User();
        user.setUsername("reader");

        when(auditLogDAO.findByUser(user)).thenReturn(List.of(new AuditLog()));

        List<AuditLog> result = auditService.getLogsByUser(user);

        assertEquals(1, result.size());
        verify(auditLogDAO).findByUser(user);
    }
}
