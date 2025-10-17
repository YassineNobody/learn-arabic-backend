package com.backend.dto.common;

import com.backend.enums.StatusApiResponse;
import lombok.Getter;

@Getter
public class ErrorResponse extends ApiResponseBase<Void> {
    private final String errorCode;
    private final String description;

    public ErrorResponse(String message, String errorCode, String description){
        super(StatusApiResponse.ERROR);
        this.errorCode = errorCode;
        this.description = description;
    }
}
