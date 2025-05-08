package com.example.library_management.dao;

import com.example.library_management.model.AuditLog;
import com.example.library_management.model.User;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogDAO implements GenericDAO<AuditLog, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<AuditLog> findById(Long id) {
        return Optional.ofNullable(entityManager.find(AuditLog.class, id));
    }

    @Override
    public List<AuditLog> findAll() {
        TypedQuery<AuditLog> query = entityManager.createQuery("SELECT a FROM AuditLog a", AuditLog.class);
        return query.getResultList();
    }

    @Override
    public void save(AuditLog auditLog) {
        entityManager.persist(auditLog);
    }

    @Override
    public void update(AuditLog auditLog) {
        entityManager.merge(auditLog);
    }

    @Override
    public void delete(AuditLog auditLog) {
        entityManager.remove(
                entityManager.contains(auditLog) ? auditLog : entityManager.merge(auditLog)
        );
    }

    public List<AuditLog> findByUser(User user) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.user = :user", AuditLog.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<AuditLog> findByBookId(Long bookId) {
        TypedQuery<AuditLog> query = entityManager.createQuery(
                "SELECT a FROM AuditLog a WHERE a.book.id = :bookId", AuditLog.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }
}
