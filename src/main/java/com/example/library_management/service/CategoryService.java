package com.example.library_management.service;

import com.example.library_management.dao.BookInfoDAO;
import com.example.library_management.dao.CategoryDAO;
import com.example.library_management.dto.CategoryDTO;
import com.example.library_management.model.Category;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    private final CategoryDAO categoryDAO;
    private final BookInfoDAO bookInfoDAO;

    public CategoryService(CategoryDAO categoryDAO, BookInfoDAO bookInfoDAO) {
        this.categoryDAO = categoryDAO;
        this.bookInfoDAO = bookInfoDAO;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = categoryDAO.findAll();
        log.info("Получено {} категорий", categories.size());
        return categories;
    }

    public Category getCategoryById(Long id) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Категория с ID {} не найдена", id);
                    throw new EntityNotFoundException("Категория с ID " + id + " не найдена");
                });
        log.info("Категория с ID {} найдена", id);
        return category;
    }

    public List<Category> getSubcategories(Long parentId) {
        List<Category> subcategories = categoryDAO.findByParentId(parentId);
        log.info("Получено {} подкатегорий для родительской категории с ID {}", subcategories.size(), parentId);
        return subcategories;
    }

    @Transactional
    public void createCategory(Category category) {
        Optional<Category> existing = categoryDAO.findByName(category.getName());
        if (existing.isPresent()) {
            log.warn("Попытка создать уже существующую категорию: '{}'", category.getName());
            throw new IllegalStateException("Категория с таким названием уже существует");
        }

        if (category.getParent() != null && category.getParent().getId() != null) {
            Long parentId = category.getParent().getId();
            Category parent = categoryDAO.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Родительская категория с ID " + parentId + " не найдена"));
            category.setParent(parent);
        }

        categoryDAO.save(category);
        log.info("Категория '{}' успешно создана", category.getName());
    }

    @Transactional
    public void updateCategory(CategoryDTO dto) {
        Category existing = categoryDAO.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));

        existing.setName(dto.getName());

        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(dto.getId())) {
                throw new IllegalArgumentException("Категория не может быть родителем самой себя");
            }
            Category parent = categoryDAO.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Родительская категория не найдена"));
            existing.setParent(parent);
        } else {
            existing.setParent(null);
        }

        categoryDAO.update(existing);
        log.info("Категория с ID {} успешно обновлена", dto.getId());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + id + " не найдена"));

        List<Category> subcategories = categoryDAO.findByParentId(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalStateException("Нельзя удалить категорию, у неё есть подкатегории");
        }

        boolean isUsedInBooks = bookInfoDAO.existsByCategoryId(id);
        if (isUsedInBooks) {
            throw new IllegalStateException("Нельзя удалить категорию, она используется в книгах");
        }

        categoryDAO.delete(category);
        log.info("Категория с ID {} удалена", id);
    }
}