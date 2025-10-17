package com.backend.service;

import com.backend.dto.user.UpdateUserRequest;
import com.backend.dto.user.UserResponse;
import com.backend.exception.UsernameAlreadyExistsException;
import com.backend.mapper.UserMapper;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import com.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse updateUser(UpdateUserRequest request){
        User currentUser = SecurityUtil.getCurrentUser();
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        currentUser.setUsername(request.getUsername());
        var saved = userRepository.save(currentUser);
        return UserMapper.toResponse(saved);
    }
}
