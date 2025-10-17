package com.backend.mapper;

import com.backend.dto.document.DocumentResponse;
import com.backend.model.Document;

import java.util.List;

public class DocumentMapper {

    public static DocumentResponse toResponse(Document document) {
        if (document == null) return null;

        return DocumentResponse.builder()
                .id(document.getId())
                .slug(document.getSlug())
                .name(document.getName())
                .description(document.getDescription())
                .urlPdf(document.getUrlPdf())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .category(
                        document.getCategory() != null
                                ? CategoryMapper.toCategoryResponse(document.getCategory(), false, false)
                                : null
                )
                .build();
    }

    public static List<DocumentResponse> toResponseList(List<Document> documents) {
        return documents == null
                ? List.of()
                : documents.stream()
                .map(DocumentMapper::toResponse)
                .toList();
    }
}
