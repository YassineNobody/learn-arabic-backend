package com.backend.controller;

import com.backend.dto.category.CategoryResponse;
import com.backend.dto.category.CreateCategoryRequest;
import com.backend.dto.common.SuccessResponse;
import com.backend.service.CategoryService;
import com.backend.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<SuccessResponse<CategoryResponse>>create(
            @RequestBody @Valid CreateCategoryRequest request
            ){
        var category = categoryService.createCategory(request);
        return ResponseEntity.status(201).body(ResponseFactory.success(category));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<CategoryResponse>>>getCategoryParent(){
        var categories = categoryService.getCategoryParent();
        return ResponseEntity.ok(ResponseFactory.success(categories));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SuccessResponse<CategoryResponse>>getBySlug(
            @PathVariable String slug
    ){
        var category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ResponseFactory.success(category));
    }
    @DeleteMapping("/{slug}")
    public ResponseEntity<SuccessResponse<String>>deleteCategory(
            @PathVariable String slug
    ){
        var message = categoryService.deleteCategory(slug);
        return ResponseEntity.ok(ResponseFactory.success(message));
    }
}
