package com.example.library_management.controller;

import com.example.library_management.dto.CategoryDTO;
import com.example.library_management.model.Category;
import com.example.library_management.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Категории", description = "Методы для управления категориями книг")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Получить все категории",
            description = "Возвращает список всех категорий. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список категорий успешно получен",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить все категории
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        log.info("Получено {} категорий из базы данных", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Получить категорию по ID",
            description = "Возвращает информацию о категории по её ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Категория найдена",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить категорию по ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "ID категории", example = "3")
            @PathVariable Long id
    ) {
        Category category = categoryService.getCategoryById(id);
        log.info("Получена категория с ID {}", id);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Получить подкатегории по ID родителя",
            description = "Возвращает список подкатегорий для категории по её ID родителя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список подкатегорий успешно получен",
                            content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Подкатегории не найдены", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Получить подкатегории по ID родителя
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(
            @Parameter(description = "ID родительской категории", example = "1")
            @PathVariable Long parentId
    ) {
        List<Category> subcategories = categoryService.getSubcategories(parentId);
        log.info("Получено {} подкатегорий для родителя с ID {}", subcategories.size(), parentId);
        return ResponseEntity.ok(subcategories);
    }

    @Operation(
            summary = "Создать новую категорию",
            description = "Создаёт новую категорию. Только для администратора.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные новой категории",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Категория успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

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

    @Operation(
            summary = "Обновить категорию",
            description = "Обновляет категорию по ID. Только для администратора.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновлённые данные категории",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Обновить категорию
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(
            @Parameter(description = "ID категории для обновления", example = "4")
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryDTO.setId(id);
        categoryService.updateCategory(categoryDTO);
        return ResponseEntity.ok("Категория успешно обновлена");
    }

    @Operation(
            summary = "Удалить категорию",
            description = "Удаляет категорию по её ID. Только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Категория успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
            }
    )

    // Удалить категорию
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "ID категории для удаления", example = "7")
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
        log.info("Категория с ID {} успешно удалена", id);
        return ResponseEntity.ok("Категория успешно удалена");
    }
}
