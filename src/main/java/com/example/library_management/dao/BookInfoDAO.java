package com.example.library_management.dao;

import com.example.library_management.model.BookInfo;
import com.example.library_management.model.Category;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class BookInfoDAO implements GenericDAO<BookInfo, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public  Optional<BookInfo> findById(Long id) {
        return Optional.ofNullable(entityManager.find(BookInfo.class, id));
    }

    @Override
    public List<BookInfo> findAll() {
        TypedQuery<BookInfo> query = entityManager.createQuery("SELECT b FROM BookInfo b", BookInfo.class);
        return query.getResultList();
    }

    @Override
    public void save(BookInfo bookInfo) {
        entityManager.persist(bookInfo);
    }

    @Override
    public void update(BookInfo bookInfo) {
        entityManager.merge(bookInfo);
    }

    @Override
    public void delete(BookInfo bookInfo) {
        entityManager.remove(
                entityManager.contains(bookInfo) ? bookInfo : entityManager.merge(bookInfo)
        );
    }

    public boolean existsByCategoryId(Long categoryId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(b) FROM BookInfo b WHERE b.category.id = :categoryId", Long.class);
        query.setParameter("categoryId", categoryId);
        Long count = query.getSingleResult();
        return count > 0;
    }
}
