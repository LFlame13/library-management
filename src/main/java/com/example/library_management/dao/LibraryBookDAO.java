package com.example.library_management.dao;

import com.example.library_management.model.LibraryBook;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class LibraryBookDAO implements GenericDAO<LibraryBook, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<LibraryBook> findById(Long id) {
        return Optional.ofNullable(entityManager.find(LibraryBook.class, id));
    }

    @Override
    public List<LibraryBook> findAll() {
        TypedQuery<LibraryBook> query = entityManager.createQuery("SELECT lb FROM LibraryBook lb", LibraryBook.class);
        return query.getResultList();
    }

    @Override
    public void save(LibraryBook libraryBook) {
        entityManager.persist(libraryBook);
    }

    @Override
    public void update(LibraryBook libraryBook) {
        entityManager.merge(libraryBook);
    }

    @Override
    @Transactional
    public void delete(LibraryBook libraryBook) {
        entityManager.remove(
                entityManager.contains(libraryBook) ? libraryBook : entityManager.merge(libraryBook)
        );
    }

    public Optional<LibraryBook> findBySerialNumber(Long serialNumber) {
        TypedQuery<LibraryBook> query = entityManager.createQuery(
                "SELECT lb FROM LibraryBook lb WHERE lb.serialNumber = :serialNumber", LibraryBook.class);
        query.setParameter("serialNumber", serialNumber);
        List<LibraryBook> libraryBooks = query.getResultList();
        return libraryBooks.stream().findFirst();
    }
}
