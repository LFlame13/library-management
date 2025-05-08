package com.example.library_management.service;

import com.example.library_management.dao.AuditLogDAO;
import com.example.library_management.model.AuditLog;
import com.example.library_management.model.LibraryBook;
import com.example.library_management.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AuditService {

    private final AuditLogDAO auditLogDAO;

    @Autowired
    public AuditService(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    @Transactional
    public void logAction(User user, LibraryBook book, String action) {
        if (user == null || book == null || action == null || action.isBlank()) {
            log.error("Ошибка логирования действия: один из параметров равен null или пустой");
            throw new IllegalArgumentException("User, book и action не могут быть null или пустыми");
        }

        AuditLog logEntry = new AuditLog();
        logEntry.setUser(user);
        logEntry.setBook(book);
        logEntry.setAction(action);

        auditLogDAO.save(logEntry);
        log.info("Пользователь с ID {} выполнил действие '{}' над книгой с ID {}",
                user.getId(), action, book.getId());
    }

    // Получить всю историю действий
    public List<AuditLog> getAllLogs() {
        log.info("Получение всей истории действий");
        return auditLogDAO.findAll();
    }


     // Получить историю по книге
    public List<AuditLog> getLogsByBookId(Long bookId) {
        List<AuditLog> logs = auditLogDAO.findByBookId(bookId);
        if (logs.isEmpty()) {

            throw new EntityNotFoundException("Логов для книги с ID " + bookId + " не найдено");
        }
        log.info("Получение истории действий для книги с ID {}", bookId);
        return logs;
    }


     // Получить историю по пользователю
    public List<AuditLog> getLogsByUser(User user) {
        List<AuditLog> logs = auditLogDAO.findByUser(user);
        if (logs.isEmpty()) {
            throw new EntityNotFoundException("Логов для пользователя " + user.getId() + " не найдено");
        }
        log.info("Получение истории действий для пользователя '{}'", user.getUsername());
        return logs;
    }



}
