package com.backend.service;

import com.backend.dto.category.CategoryResponse;
import com.backend.dto.category.CreateCategoryRequest;
import com.backend.exception.CategoryAlreadyExist;
import com.backend.mapper.CategoryMapper;
import com.backend.model.Category;
import com.backend.repository.CategoryRepository;
import com.backend.util.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private String normalizeName(String name) {
        return name == null ? null : name.trim().toLowerCase();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String normalizedName = normalizeName(request.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }

        if (categoryRepository.existsByName(normalizedName)) {
            throw new CategoryAlreadyExist("Catégorie déjà existante");
        }

        String slug = SlugUtil.generateSlug(normalizedName);
        if (categoryRepository.existsBySlug(slug)) {
            throw new CategoryAlreadyExist("Une catégorie avec ce slug existe déjà");
        }

        Category.CategoryBuilder builder = Category.builder()
                .name(normalizedName)
                .slug(slug)
                .description(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie parente introuvable"));
            builder.parent(parent);
        }

        Category saved = categoryRepository.save(builder.build());
        return CategoryMapper.toCategoryResponse(saved);
    }
    private void initializeRecursively(Category category) {
        category.getChildren().size(); // force init
        category.getChildren().forEach(this::initializeRecursively);
    }
    private Category findBySlugOrThrow(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryParent(){
        Set<Category> roots = categoryRepository.findByParentIsNull();
        roots.forEach(this::initializeRecursively); // ⚡ force init des enfants
       return CategoryMapper.toCategoryResponseList(roots);
    }


    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug){
        var cat = findBySlugOrThrow(slug);
        return CategoryMapper.toCategoryResponse(cat);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteCategory(String slug){
        var cat = findBySlugOrThrow(slug);
        categoryRepository.delete(cat);
        return "Catégorie supprimée";
    }
}
