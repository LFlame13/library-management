package com.example.library_management.service;

import com.example.library_management.dao.BookInfoDAO;
import com.example.library_management.dao.CategoryDAO;
import com.example.library_management.dao.LibraryBookDAO;
import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.dto.UpdateBookInfoDTO;
import com.example.library_management.mapper.LibraryBookMapper;
import com.example.library_management.model.BookInfo;
import com.example.library_management.model.Category;
import com.example.library_management.model.LibraryBook;
import com.example.library_management.model.LibraryBook.BookStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryBookServiceTest {

    @Mock private LibraryBookDAO libraryBookDAO;
    @Mock private CategoryDAO categoryDAO;
    @Mock private BookInfoDAO bookInfoDAO;
    @Mock private LibraryBookMapper libraryBookMapper;

    @InjectMocks private LibraryBookService libraryBookService;

    private LibraryBook libraryBook;
    private BookInfo bookInfo;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setId(1L);
        category.setName("Боевик");

        bookInfo = new BookInfo();
        bookInfo.setId(1L);
        bookInfo.setTitle("Book Title");
        bookInfo.setAuthor("Author Name");
        bookInfo.setCategory(category);

        libraryBook = new LibraryBook();
        libraryBook.setId(1L);
        libraryBook.setSerialNumber(123L);
        libraryBook.setBookInfo(bookInfo);
        libraryBook.setStatus(BookStatus.AVAILABLE);
    }

    @Test
    void testGetAllBooks() {
        when(libraryBookDAO.findAll()).thenReturn(List.of(libraryBook));
        when(libraryBookMapper.toDTO(any())).thenReturn(new LibraryBookDTO());

        List<LibraryBookDTO> result = libraryBookService.getAllBooks();
        assertEquals(1, result.size());
        verify(libraryBookDAO).findAll();
    }

    @Test
    void testGetBookEntityById_Success() {
        when(libraryBookDAO.findById(1L)).thenReturn(Optional.of(libraryBook));
        LibraryBook result = libraryBookService.getBookEntityById(1L);
        assertEquals(libraryBook, result);
    }

    @Test
    void testGetBookEntityById_NotFound() {
        when(libraryBookDAO.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> libraryBookService.getBookEntityById(2L));
    }

    @Test
    void testAddFullBook_Success() {
        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));
        when(libraryBookDAO.findBySerialNumber(123L)).thenReturn(Optional.empty());

        libraryBookService.addFullBook("Book Title", "Author Name", 1L, 123L);

        verify(bookInfoDAO).save(any(BookInfo.class));
        verify(libraryBookDAO).save(any(LibraryBook.class));
    }

    @Test
    void testAddFullBook_DuplicateSerialNumber() {
        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));
        when(libraryBookDAO.findBySerialNumber(123456L)).thenReturn(Optional.of(libraryBook));

        assertThrows(IllegalStateException.class, () ->
                libraryBookService.addFullBook("Title", "Author", 1L, 123456L));
    }

    @Test
    void testDeleteLibraryBook_MarksAsDeletedAndSkipsDeletionIfNoCopiesLeft() {
        when(libraryBookDAO.findById(1L)).thenReturn(Optional.of(libraryBook));
        libraryBook.setStatus(LibraryBook.BookStatus.AVAILABLE);

        libraryBookService.deleteLibraryBook(1L);

        assertEquals(LibraryBook.BookStatus.DELETED, libraryBook.getStatus());
        verify(libraryBookDAO).update(libraryBook);
        verify(bookInfoDAO, never()).delete(any());
    }

    @Test
    void testUpdateBookInfo_Success() {
        UpdateBookInfoDTO dto = new UpdateBookInfoDTO();
        dto.setBookInfoId(1L);
        dto.setTitle("Updated Title");
        dto.setAuthor("Updated Author");
        dto.setCategoryId(1L);

        when(libraryBookDAO.findById(1L)).thenReturn(Optional.of(libraryBook));
        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));

        libraryBookService.updateBookInfo(1L, dto);

        assertEquals("Updated Title", bookInfo.getTitle());
        assertEquals("Updated Author", bookInfo.getAuthor());
        assertEquals(category, bookInfo.getCategory());
        verify(bookInfoDAO).update(bookInfo);
    }

    @Test
    void testUpdateBookInfo_BookInfoMismatch() {
        UpdateBookInfoDTO dto = new UpdateBookInfoDTO();
        dto.setBookInfoId(999L);

        when(libraryBookDAO.findById(1L)).thenReturn(Optional.of(libraryBook));

        assertThrows(IllegalArgumentException.class, () -> libraryBookService.updateBookInfo(1L, dto));
    }
}
