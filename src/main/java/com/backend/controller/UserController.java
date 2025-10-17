package com.backend.controller;

import com.backend.dto.common.SuccessResponse;
import com.backend.dto.user.UpdateUserRequest;
import com.backend.dto.user.UserResponse;
import com.backend.service.UserService;
import com.backend.util.ResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PutMapping
    public ResponseEntity<SuccessResponse<UserResponse>>updateUser(
            @RequestBody @Valid UpdateUserRequest request
            ){
        var user = userService.updateUser(request);
        return ResponseEntity.ok(ResponseFactory.success(user));
    }
}
