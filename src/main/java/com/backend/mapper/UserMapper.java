package com.backend.mapper;

import com.backend.dto.auth.RegisterUserRequest;
import com.backend.dto.user.UserResponse;
import com.backend.model.User;

public class UserMapper {
    static public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    static  public User toModel(RegisterUserRequest request){
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
    }
}
