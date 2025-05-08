package com.example.library_management.dao;

import com.example.library_management.model.Rental;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class RentalDAO implements GenericDAO<Rental, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Rental> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Rental.class, id));
    }

    @Override
    public List<Rental> findAll() {
        TypedQuery<Rental> query = entityManager.createQuery("SELECT r FROM Rental r", Rental.class);
        return query.getResultList();
    }

    @Override
    public void save(Rental rental) {
        entityManager.persist(rental);
    }

    @Override
    public void update(Rental rental) {
        entityManager.merge(rental);
    }

    @Override
    public void delete(Rental rental) {
        entityManager.remove(
                entityManager.contains(rental) ? rental : entityManager.merge(rental)
        );
    }

    public List<Rental> findByUserId(Long userId) {
        TypedQuery<Rental> query = entityManager.createQuery(
                "SELECT r FROM Rental r WHERE r.user.id = :userId", Rental.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<Rental> findByBookId(Long bookId) {
        TypedQuery<Rental> query = entityManager.createQuery(
                "SELECT r FROM Rental r WHERE r.libraryBook.id = :bookId", Rental.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    public List<Rental> findActiveByUserId(Long userId) {
        TypedQuery<Rental> query = entityManager.createQuery(
                "SELECT r FROM Rental r WHERE r.user.id = :userId AND r.returnedAt IS NULL", Rental.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
