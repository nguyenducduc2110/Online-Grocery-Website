package com.springboot3.Web.of.spring.boot.auth.service;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserUpdateRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    public abstract UserResponse getMyInfo();
    List<UserResponse> getUsers();
    UserResponse getUser(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest userDto);
    UserResponse createUser(UserCreationRequest userCreationRequest);
    void deleteUser(Long id);
}
