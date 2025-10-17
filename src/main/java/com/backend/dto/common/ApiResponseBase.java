package com.backend.dto.common;

import com.backend.enums.StatusApiResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class ApiResponseBase <T> {
    protected StatusApiResponse status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime timestamp;


    public ApiResponseBase(StatusApiResponse status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
