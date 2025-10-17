package com.backend.repository;

import com.backend.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 🔍 Trouver un document par son slug
    Optional<Document> findBySlug(String slug);

    // 🔍 Trouver un document par son nom
    Optional<Document> findByName(String name);

    // 📚 Liste tous les documents d’une catégorie donnée
    List<Document> findByCategory_Id(Long categoryId);
    Page<Document> findByCategory_Id(Long categoryId, Pageable pageable);
    // ⚡ Vérifie existence par nom ou slug
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    List<Document> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
