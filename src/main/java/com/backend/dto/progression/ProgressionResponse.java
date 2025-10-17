package com.backend.dto.progression;


import com.backend.dto.document.DocumentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionResponse {
    private Long id;
    private List<DocumentResponse> inProgress;
    private List<DocumentResponse> complete;
    private List<DocumentResponse>favorites;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
