package com.example.library_management.controller;

import com.example.library_management.dto.CategoryDTO;
import com.example.library_management.launch.Main;
import com.example.library_management.model.Category;
import com.example.library_management.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("category-test")
@SpringBootTest
@ContextConfiguration(classes = {Main.class, TestConfig.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCategories_shouldReturnList() throws Exception {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Science");

        when(categoryService.getAllCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/api/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cat.getId()))
                .andExpect(jsonPath("$[0].name").value("Science"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category cat = new Category();
        cat.setId(2L);
        cat.setName("History");

        when(categoryService.getCategoryById(2L)).thenReturn(cat);

        mockMvc.perform(get("/api/categories/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("History"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_shouldReturnSuccessMessage() throws Exception {
        CategoryDTO dto = new CategoryDTO(null, "Art", null);

        mockMvc.perform(post("/api/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Категория успешно создана"));

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_shouldReturnSuccessMessage() throws Exception {
        CategoryDTO dto = new CategoryDTO(5L, "Updated Category", null);

        mockMvc.perform(put("/api/categories/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Категория успешно обновлена"));

        verify(categoryService, times(1)).updateCategory(argThat(updated -> updated.getId().equals(5L)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/categories/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Категория успешно удалена"));

        verify(categoryService, times(1)).deleteCategory(10L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSubcategories_shouldReturnList() throws Exception {
        Category sub = new Category();
        sub.setId(3L);
        sub.setName("Subcategory");

        when(categoryService.getSubcategories(1L)).thenReturn(List.of(sub));

        mockMvc.perform(get("/api/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].name").value("Subcategory"));
    }
}
