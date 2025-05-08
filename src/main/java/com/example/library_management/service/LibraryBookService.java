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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LibraryBookService {

    private final LibraryBookDAO libraryBookDAO;
    private final CategoryDAO categoryDAO;
    private final BookInfoDAO bookInfoDAO;
    private final LibraryBookMapper libraryBookMapper;


    @Autowired
    public LibraryBookService(LibraryBookDAO libraryBookDAO, CategoryDAO categoryDAO, BookInfoDAO bookInfoDAO, LibraryBookMapper libraryBookMapper) {
        this.libraryBookDAO = libraryBookDAO;
        this.categoryDAO = categoryDAO;
        this.bookInfoDAO = bookInfoDAO;
        this.libraryBookMapper = libraryBookMapper;
    }

    // Получить все книги
    public List<LibraryBookDTO> getAllBooks() {
        List<LibraryBook> books = libraryBookDAO.findAll()
                .stream()
                .filter(book -> book.getStatus() != BookStatus.DELETED)
                .collect(Collectors.toList());

        return books.stream()
                .map(libraryBookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public LibraryBook getBookEntityById(Long id) {
        return libraryBookDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Книга с ID {} не найдена", id);
                    throw new EntityNotFoundException("Книга с ID " + id + " не найдена");
                });
    }

    public LibraryBookDTO getBookDTOById(Long id) {
        LibraryBook book = getBookEntityById(id);
        return libraryBookMapper.toDTO(book);
    }




    @Transactional
    public void addFullBook(String title, String author, Long categoryId, Long serialNumber) {
        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Категория с ID {} не найдена при добавлении книги", categoryId);
                    throw new EntityNotFoundException("Категория с ID " + categoryId + " не найдена");
                });

        if (libraryBookDAO.findBySerialNumber(serialNumber).isPresent()) {
            throw new IllegalStateException("Серийный номер " + serialNumber + " уже существует");
        }

        BookInfo bookInfo = new BookInfo();
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setCategory(category);
        bookInfoDAO.save(bookInfo);

        LibraryBook libraryBook = new LibraryBook();
        libraryBook.setBookInfo(bookInfo);
        libraryBook.setSerialNumber(serialNumber);
        libraryBook.setStatus(BookStatus.AVAILABLE);
        libraryBookDAO.save(libraryBook);

        log.info("Книга '{}' автора '{}' успешно добавлена с серийным номером {}", title, author, serialNumber);
    }

    @Transactional
    public void deleteLibraryBook(Long bookId) {
        LibraryBook book = getBookEntityById(bookId);

        if (book.getStatus() == BookStatus.RENTED) {
            throw new IllegalStateException("Нельзя удалить книгу, она сейчас в аренде");
        }

        book.setStatus(BookStatus.DELETED);
        libraryBookDAO.update(book);

        log.info("Книга с ID {} помечена как удалённая", bookId);

    }

    @Transactional
    public void updateBookInfo(Long bookId, UpdateBookInfoDTO updateBookInfoDTO) {
        LibraryBook book = getBookEntityById(bookId);

        BookInfo bookInfo = book.getBookInfo();
        if (bookInfo.getId() != updateBookInfoDTO.getBookInfoId()) {
            throw new IllegalArgumentException("ID книги не совпадает с ID в запросе");
        }

        bookInfo.setTitle(updateBookInfoDTO.getTitle());
        bookInfo.setAuthor(updateBookInfoDTO.getAuthor());

        Category category = categoryDAO.findById(updateBookInfoDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + updateBookInfoDTO.getCategoryId() + " не найдена"));
        bookInfo.setCategory(category);

        bookInfoDAO.update(bookInfo);
        log.info("Информация о книге '{}' автора '{}' обновлена", bookInfo.getTitle(), bookInfo.getAuthor());
    }
}
