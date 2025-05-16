package com.example.library_management.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO для категорий")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Schema(description = "Уникальный идентификатор категории", example = "1", hidden = true)
    private Long id;

    @NotBlank(message = "Название категории обязательно")
    @Schema(description = "Название категории", example = "Юмор")
    private String name;

    @Schema(description = "Родитель категории", example = "1")
    private Long parentId;
}
