package com.example.library_management.dao;

import com.example.library_management.model.UserRole;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRoleDAO implements GenericDAO<UserRole, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserRole> findById(Long id) {
        return Optional.ofNullable(entityManager.find(UserRole.class, id));
    }

    @Override
    public List<UserRole> findAll() {
        TypedQuery<UserRole> query = entityManager.createQuery(
                "SELECT ur FROM UserRole ur", UserRole.class);
        return query.getResultList();
    }

    @Override
    public void save(UserRole userRole) {
        entityManager.persist(userRole);
    }

    @Override
    public void update(UserRole userRole) {
        entityManager.merge(userRole);
    }

    @Override
    public void delete(UserRole userRole) {
        entityManager.remove(
                entityManager.contains(userRole) ? userRole : entityManager.merge(userRole)
        );
    }

    public void deleteByUserId(Long userId) {
        List<UserRole> userRoles = findByUserId(userId);
        for (UserRole userRole : userRoles) {
            delete(userRole);
        }
    }

    public List<UserRole> findByUserId(Long userId) {
        TypedQuery<UserRole> query = entityManager.createQuery(
                "SELECT ur FROM UserRole ur WHERE ur.user.id = :userId", UserRole.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
