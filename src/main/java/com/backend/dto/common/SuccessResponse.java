package com.backend.dto.common;


import com.backend.enums.StatusApiResponse;
import lombok.Getter;

@Getter
public class SuccessResponse <T> extends ApiResponseBase<T>{
    private final T data;
    public SuccessResponse(T data){
        super(StatusApiResponse.SUCCESS);
        this.data = data;

    }
}