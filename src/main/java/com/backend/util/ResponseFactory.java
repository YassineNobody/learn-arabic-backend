package com.backend.util;

import com.backend.dto.common.*;

public class ResponseFactory {

    public static <T> SuccessResponse<T> success(
            T data
    ) {
        return new SuccessResponse<>(data);
    }

    public static ErrorResponse error(
            String message, String errorCode, String description
    ){
        return new ErrorResponse(message, errorCode, description);
    }

}
