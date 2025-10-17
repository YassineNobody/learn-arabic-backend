package com.backend.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDocumentRequest {
    private String name;
    private String description;
    private Long CategoryId;
    private MultipartFile file;
}
