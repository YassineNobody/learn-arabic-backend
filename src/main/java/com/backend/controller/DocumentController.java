package com.backend.controller;

import com.backend.dto.common.PagedResponse;
import com.backend.dto.common.SuccessResponse;
import com.backend.dto.document.CreateDocumentRequest;
import com.backend.dto.document.DocumentResponse;
import com.backend.dto.document.UpdateDocumentRequest;
import com.backend.service.DocumentService;
import com.backend.util.RequestUtil;
import com.backend.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SuccessResponse<DocumentResponse>> create(
            @ModelAttribute @Valid CreateDocumentRequest request
    ) {
        var doc = documentService.createDocument(request);
        return ResponseEntity.status(201).body(ResponseFactory.success(doc));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SuccessResponse<DocumentResponse>>getBySlug(
            @PathVariable String slug
    ){
        var doc = documentService.getBySlug(slug);
        return ResponseEntity.ok(ResponseFactory.success(doc));
    }

    @GetMapping("/category")
    public ResponseEntity<SuccessResponse<PagedResponse<DocumentResponse>>>getByCategory(
            @RequestParam(name = "slug") String slug,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ){
        Pageable pageable = RequestUtil.getPageable(page, size, true);
        var docs = documentService.getDocumentsByCategorySlug(slug, pageable);
        return ResponseEntity.ok(ResponseFactory.success(docs));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<SuccessResponse<String>>deleteDocument(
            @PathVariable String slug
    ){
        var message = documentService.deleteDocument(slug);
        return ResponseEntity.ok(ResponseFactory.success(message));
    }


    @PutMapping("/{slug}")
    public ResponseEntity<SuccessResponse<DocumentResponse>>updateDocument(
            @PathVariable String slug,
            @ModelAttribute @Valid UpdateDocumentRequest request
            ){
        var doc = documentService.updateDocument(slug, request);
        return ResponseEntity.ok(ResponseFactory.success(doc));
    }

    @GetMapping("/latest")
    public ResponseEntity<SuccessResponse<List<DocumentResponse>>>getLastestDocuments(
            @RequestParam(defaultValue = "5") int limit
    ){
        var docs = documentService.getLastDocuments(limit);
        return ResponseEntity.ok(ResponseFactory.success(docs));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<SuccessResponse<List<DocumentResponse>>>getAllDocumentsByAdmin(){
        var docs = documentService.getAllDocumentsByAdmin();
        return ResponseEntity.ok(ResponseFactory.success(docs));
    }
}
