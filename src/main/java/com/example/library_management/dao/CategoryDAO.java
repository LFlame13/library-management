package com.example.library_management.dao;

import com.example.library_management.model.Category;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDAO implements GenericDAO<Category, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Category.class, id));
    }

    @Override
    public List<Category> findAll() {
        TypedQuery<Category> query = entityManager.createQuery("SELECT c FROM Category c", Category.class);
        return query.getResultList();
    }

    @Override
    public void save(Category category) {
        entityManager.persist(category);
    }

    @Override
    public void update(Category category) {
        entityManager.merge(category);
    }

    @Override
    public void delete(Category category) {
        entityManager.remove(
                entityManager.contains(category) ? category : entityManager.merge(category)
        );
    }

    public List<Category> findByParentId(Long parentId) {
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c WHERE c.parent.id = :parentId", Category.class);
        query.setParameter("parentId", parentId);
        return query.getResultList();
    }

    public Optional<Category> findByName(String name) {
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c WHERE c.name = :name", Category.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }
}
