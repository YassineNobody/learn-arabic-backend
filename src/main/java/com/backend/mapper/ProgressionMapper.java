package com.backend.mapper;

import com.backend.dto.progression.ProgressionResponse;
import com.backend.model.Progression;

public class ProgressionMapper {
    static public ProgressionResponse toResponse(Progression progression){
        return ProgressionResponse.builder()
                .id(progression.getId())
                .inProgress(DocumentMapper.toResponseList(progression.getInProgress()))
                .complete(DocumentMapper.toResponseList(progression.getComplete()))
                .favorites(DocumentMapper.toResponseList(progression.getFavorites()))
                .createdAt(progression.getCreatedAt())
                .updatedAt(progression.getUpdatedAt())
                .build();
    }
}
