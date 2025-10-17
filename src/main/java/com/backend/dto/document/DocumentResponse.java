package com.backend.dto.document;

import com.backend.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {
    private Long id;
    private String slug;
    private String name;
    private String description;
    private CategoryResponse category;
    private String urlPdf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
