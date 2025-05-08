package com.example.library_management.service;

import com.example.library_management.dao.LibraryBookDAO;
import com.example.library_management.dao.RentalDAO;
import com.example.library_management.dto.RentalDTO;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.model.LibraryBook;
import com.example.library_management.model.Rental;
import com.example.library_management.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RentalService {

    private final RentalDAO rentalDAO;
    private final LibraryBookDAO libraryBookDAO;
    private final AuditService auditService;
    private final RentalMapper rentalMapper;

    @Autowired
    public RentalService(RentalDAO rentalDAO, LibraryBookDAO libraryBookDAO, AuditService auditService, RentalMapper rentalMapper) {
        this.rentalDAO = rentalDAO;
        this.libraryBookDAO = libraryBookDAO;
        this.auditService = auditService;
        this.rentalMapper = rentalMapper;
    }


    //Арендовать книгу пользователем
    @Transactional
    public void rentBook(User user, Long bookId) {
        LibraryBook book = libraryBookDAO.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Ошибка: Книга с ID '{}' не найдена при попытке аренды", bookId);
                    throw new EntityNotFoundException("Книга не найдена");
                });

        if (book.getStatus() != LibraryBook.BookStatus.AVAILABLE) {
            log.error("Ошибка: Книга с ID '{}' недоступна для аренды. Текущий статус: {}", bookId, book.getStatus());
            throw new EntityNotFoundException("Книга недоступна для аренды");
        }

        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime dueDate = now.plusDays(7); //срок возврата через 7 дней

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setLibraryBook(book);
        rental.setRentedAt(now);
        rental.setDueDate(dueDate);

        book.setStatus(LibraryBook.BookStatus.RENTED);
        libraryBookDAO.update(book);

        rentalDAO.save(rental);

        auditService.logAction(user, book, "BOOK_RENTED");

        log.info("Книга с ID '{}' успешно арендована пользователем '{}'. Дата возврата: {}", bookId, user.getUsername(), dueDate);
    }

    // Возврат
    @Transactional
    public void returnBook(Long bookId, User currentUser) {
        LibraryBook book = libraryBookDAO.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Ошибка: Книга с ID '{}' не найдена", bookId);
                    throw new EntityNotFoundException("Книга не найдена");
                });

        List<Rental> rentals = rentalDAO.findByBookId(bookId);

        if (rentals.isEmpty()) {
            log.error("Ошибка: Нет записей аренды для книги с ID '{}'", bookId);
            throw new EntityNotFoundException("Книга не арендована");
        }

        if (book.getStatus() == LibraryBook.BookStatus.DELETED) {
            log.error("Ошибка: Книга с ID '{}' помечена как удалённая", bookId);
            throw new IllegalStateException("Книга удалена");
        }

        if (book.getStatus() == LibraryBook.BookStatus.AVAILABLE) {
            log.error("Ошибка: Книга с ID '{}' доступна, но попытка возврата", bookId);
            throw new IllegalStateException("Книга уже доступна — возврат невозможен");
        }

        Rental rental = rentals.stream()
                .filter(r -> r.getReturnedAt() == null)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Ошибка: Книга с ID '{}' уже возвращена", bookId);
                    throw new IllegalStateException("Книга уже возвращена");
                });

        if (!rental.getUser().getId().equals(currentUser.getId())) {
            log.warn("Пользователь '{}' пытался вернуть чужую аренду книги с ID '{}'", currentUser.getUsername(), bookId);
            throw new SecurityException("Вы не можете вернуть чужую книгу");
        }

        rental.setReturnedAt(LocalDateTime.now().withNano(0));
        book.setStatus(LibraryBook.BookStatus.AVAILABLE);
        libraryBookDAO.update(book);
        rentalDAO.update(rental);

        auditService.logAction(currentUser, book, "BOOK_RETURNED");

        log.info("Книга с ID '{}' успешно возвращена пользователем '{}'.", bookId, currentUser.getUsername());
    }

     //Получить просроченные аренды (не возвращены и дата уже прошла)
    public List<RentalDTO> getOverdueRentals() {
        List<RentalDTO> overdueRentals = rentalDAO.findAll().stream()
                .filter(r -> r.getReturnedAt() == null && r.getDueDate().isBefore(LocalDateTime.now()))
                .map(rentalMapper::toDTO)
                .toList();
        log.info("Запрашиваются просроченные аренды: найдено {} просроченных аренды", overdueRentals.size());
        return overdueRentals;
    }

    public List<Rental> getRentalsByUser(Long userId) {
        List<Rental> rentals = rentalDAO.findByUserId(userId);
        if (rentals == null || rentals.isEmpty()) {
            log.warn("Аренды для пользователя с ID '{}' не найдены", userId);
            throw new EntityNotFoundException("Аренды для пользователя не найдены");
        }
        log.info("Запрашиваются аренды для пользователя с ID '{}'. Найдено {} аренд", userId, rentals.size());
        return rentals;
    }

    public List<Rental> getRentalsByBook(Long bookId) {
        List<Rental> rentals = rentalDAO.findByBookId(bookId);
        if (rentals.isEmpty()) {
            log.warn("Аренды для книги с ID '{}' не найдены", bookId);
            throw new EntityNotFoundException("Аренды для книги не найдены");
        }
        log.info("Запрашиваются аренды для книги с ID '{}'. Найдено {} аренды", bookId, rentals.size());
        return rentals;
    }

    // Все аренды
    public List<Rental> getAllRentals() {
        List<Rental> rentals = rentalDAO.findAll();
        log.info("Запрашиваются все аренды. Найдено {} аренды", rentals.size());
        return rentals;
    }


}