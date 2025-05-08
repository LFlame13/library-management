package com.example.library_management.controller;

import com.example.library_management.dto.CategoryDTO;
import com.example.library_management.model.Category;
import com.example.library_management.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    // Получить все категории
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        log.info("Получено {} категорий из базы данных", categories.size());
        return ResponseEntity.ok(categories);
    }

    // Получить категорию по ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        log.info("Получена категория с ID {}", id);
        return ResponseEntity.ok(category);
    }

    // Получить подкатегории по ID родителя
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long parentId) {
        List<Category> subcategories = categoryService.getSubcategories(parentId);
        log.info("Получено {} подкатегорий для родителя с ID {}", subcategories.size(), parentId);
        return ResponseEntity.ok(subcategories);
    }

    // Создать новую категорию
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null) {
            Category parent = new Category();
            parent.setId(categoryDTO.getParentId());
            category.setParent(parent);
        }
        categoryService.createCategory(category);
        log.info("Создана новая категория: '{}'", categoryDTO.getName());
        return ResponseEntity.ok("Категория успешно создана");
    }

    // Обновить категорию
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        categoryService.updateCategory(categoryDTO);
        return ResponseEntity.ok("Категория успешно обновлена");
    }

    // Удалить категорию
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        log.info("Категория с ID {} успешно удалена", id);
        return ResponseEntity.ok("Категория успешно удалена");
    }
}
