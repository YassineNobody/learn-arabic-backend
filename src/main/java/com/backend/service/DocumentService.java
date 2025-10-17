package com.backend.service;

import com.backend.dto.common.PagedResponse;
import com.backend.dto.document.CreateDocumentRequest;
import com.backend.dto.document.DocumentResponse;
import com.backend.dto.document.UpdateDocumentRequest;
import com.backend.exception.ResourceConflictException;
import com.backend.mapper.DocumentMapper;
import com.backend.model.Category;
import com.backend.model.Document;
import com.backend.repository.CategoryRepository;
import com.backend.repository.DocumentRepository;
import com.backend.util.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentResponse createDocument(CreateDocumentRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));

        String normalizedName = request.getName().trim().toLowerCase();

        if (documentRepository.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Un document avec ce nom existe déjà");
        }

        if (!"application/pdf".equalsIgnoreCase(request.getFile().getContentType())) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés.");
        }

        String slug = SlugUtil.generateSlug(normalizedName);
        if (documentRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Un document avec ce slug existe déjà");
        }

        String urlPdf = cloudinaryService.uploadPdf(request.getFile(), slug);

        Document document = Document.builder()
                .name(normalizedName)
                .description(request.getDescription().trim())
                .slug(slug)
                .category(category)
                .urlPdf(urlPdf)
                .build();

        Document saved = documentRepository.save(document);
        return DocumentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DocumentResponse getBySlug(String slug) {
        Document doc = documentRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Document introuvable"));
        return DocumentMapper.toResponse(doc);
    }

    @Transactional(readOnly = true)
    public PagedResponse<DocumentResponse> getDocumentsByCategorySlug(String slugCategory, Pageable pageable) {
        Category category = categoryRepository.findBySlug(slugCategory)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));

        var pageDocs = documentRepository.findByCategory_Id(category.getId(), pageable)
                .map(DocumentMapper::toResponse);

        return PagedResponse.fromPage(pageDocs);
    }

    /**
     * 🔥 Supprime un document par son slug.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDocument(String slug) {
        Document document = documentRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Document introuvable"));

        documentRepository.delete(document);
        return "Document supprimé avec succès";
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentResponse updateDocument(String slug, UpdateDocumentRequest request) {
        Document document = documentRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Document introuvable"));

        // === Nom & Slug ===
        if (request.getName() != null && !request.getName().isBlank()) {
            String normalizedName = request.getName().trim().toLowerCase();

            boolean sameName = normalizedName.equals(document.getName());
            if (!sameName && documentRepository.existsByName(normalizedName)) {
                throw new ResourceConflictException("Un document avec ce nom existe déjà");
            }

            String newSlug = SlugUtil.generateSlug(normalizedName);
            boolean sameSlug = newSlug.equals(document.getSlug());
            if (!sameSlug && documentRepository.existsBySlug(newSlug)) {
                throw new ResourceConflictException("Un document avec ce slug existe déjà");
            }

            document.setName(normalizedName);
            document.setSlug(newSlug);
        }

        // === Description ===
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription().trim());
        }

        // === Catégorie ===
        if (request.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
            document.setCategory(newCategory);
        }

        // === Fichier PDF ===
        if (request.getFile() != null) {
            if (!"application/pdf".equalsIgnoreCase(request.getFile().getContentType())) {
                throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés.");
            }

            String newUrl = cloudinaryService.uploadPdf(request.getFile(), document.getSlug());
            document.setUrlPdf(newUrl);
        }

        Document saved = documentRepository.save(document);
        return DocumentMapper.toResponse(saved);
    }


    @Transactional(readOnly = true)
    public List<DocumentResponse> getLastDocuments(int limit){
        var pageable = Pageable.ofSize(limit);
        var docs = documentRepository.findAllByOrderByCreatedAtDesc(pageable);
        return DocumentMapper.toResponseList(docs);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<DocumentResponse> getAllDocumentsByAdmin(){
        var docs = documentRepository.findAll();
        return DocumentMapper.toResponseList(docs);
    }
}
