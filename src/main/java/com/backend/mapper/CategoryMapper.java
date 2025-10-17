package com.backend.mapper;

import com.backend.dto.category.CategoryResponse;
import com.backend.model.Category;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category category) {
        return toCategoryResponse(category, true, true);
    }

    public static CategoryResponse toCategoryResponse(
            Category category,
            boolean includeParent,
            boolean includeChildren
    ) {
        if (category == null) return null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .count(category.getDocuments() != null
                        ? category.getDocuments().size()
                        : 0)
                .parent(
                        includeParent && category.getParent() != null
                                ? toShallowCategoryResponse(category.getParent())
                                : null
                )
                .children(
                        includeChildren
                                ? category.getChildren().stream()
                                .map(child -> toCategoryResponse(child, false, true))
                                .collect(Collectors.toList())
                                : List.of()
                )
                .build();
    }

    private static CategoryResponse toShallowCategoryResponse(Category category) {
        if (category == null) return null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .build();
    }

    public static Set<CategoryResponse> toCategoryResponseSet(Set<Category> categories) {
        return categories == null
                ? Set.of() // ✅ correction ici
                : categories.stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toSet());
    }
    public static List<CategoryResponse> toCategoryResponseList(Set<Category> categories) {
        return categories == null
                ? List.of()
                : categories.stream()
                .map(CategoryMapper::toCategoryResponse)
                .toList();
    }

}
