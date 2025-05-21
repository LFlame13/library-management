package com.example.library_management.service;

import com.example.library_management.dao.BookInfoDAO;
import com.example.library_management.dao.CategoryDAO;
import com.example.library_management.dto.CategoryDTO;
import com.example.library_management.model.Category;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private BookInfoDAO bookInfoDAO;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_returnsList() {
        List<Category> mockList = List.of(new Category(), new Category());
        when(categoryDAO.findAll()).thenReturn(mockList);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryDAO).findAll();
    }

    @Test
    void getCategoryById_existingId_returnsCategory() {
        Category category = new Category();
        category.setId(1L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getCategoryById_notFound_throwsException() {
        when(categoryDAO.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryById(99L);
        });

        assertEquals("Категория с ID 99 не найдена", ex.getMessage());
    }

    @Test
    void getSubcategories_returnsList() {
        List<Category> subcats = List.of(new Category(), new Category());
        when(categoryDAO.findByParentId(1L)).thenReturn(subcats);

        List<Category> result = categoryService.getSubcategories(1L);

        assertEquals(2, result.size());
    }

    @Test
    void createCategory_success() {
        Category category = new Category();
        category.setName("Наука");

        when(categoryDAO.findByName("Наука")).thenReturn(Optional.empty());

        categoryService.createCategory(category);

        verify(categoryDAO).save(category);
    }

    @Test
    void createCategory_duplicate_throwsException() {
        Category existing = new Category();
        existing.setName("Фантастика");

        when(categoryDAO.findByName("Фантастика")).thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            categoryService.createCategory(existing);
        });

        assertEquals("Категория с таким названием уже существует", ex.getMessage());
    }

    @Test
    void updateCategory_success() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old Name");

        Category parent = new Category();
        parent.setId(2L);

        CategoryDTO dto = new CategoryDTO(1L, "New Name", 2L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryDAO.findById(2L)).thenReturn(Optional.of(parent));

        categoryService.updateCategory(dto);

        assertEquals("New Name", existing.getName());
        assertEquals(parent, existing.getParent());
        verify(categoryDAO).update(existing);
    }

    @Test
    void updateCategory_parentIsSelf_throwsException() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Category");

        CategoryDTO dto = new CategoryDTO(1L, "Category", 1L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(dto);
        });

        assertEquals("Категория не может быть родителем самой себя", ex.getMessage());
        verify(categoryDAO, never()).update(any());
    }

    @Test
    void deleteCategory_success() {
        Category category = new Category();
        category.setId(1L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));
        when(categoryDAO.findByParentId(1L)).thenReturn(Collections.emptyList());
        when(bookInfoDAO.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryDAO).delete(category);
    }

    @Test
    void deleteCategory_hasSubcategories_throwsException() {
        Category category = new Category();
        category.setId(1L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));
        when(categoryDAO.findByParentId(1L)).thenReturn(List.of(new Category()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals("Нельзя удалить категорию, у неё есть подкатегории", ex.getMessage());
    }

    @Test
    void deleteCategory_usedInBooks_throwsException() {
        Category category = new Category();
        category.setId(1L);

        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));
        when(categoryDAO.findByParentId(1L)).thenReturn(Collections.emptyList());
        when(bookInfoDAO.existsByCategoryId(1L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals("Нельзя удалить категорию, она используется в книгах", ex.getMessage());
    }
}
