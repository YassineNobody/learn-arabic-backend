package com.backend.repository;

import com.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 🔍 Trouver une catégorie par son slug
    Optional<Category> findBySlug(String slug);

    // 🔍 Trouver une catégorie par son nom (utile pour éviter doublons)
    Optional<Category> findByName(String name);

    // 📁 Liste les sous-catégories d’un parent
    Set<Category> findByParent_Id(Long parentId);

    // 📁 Liste les catégories racines (sans parent)
    Set<Category> findByParentIsNull();

    // ⚡ Vérifie existence par nom ou slug
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
