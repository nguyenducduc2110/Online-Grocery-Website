package com.springboot3.Web.of.spring.boot.auth.controller;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserUpdateRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse, Void> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
        log.info("Controller:Create User");

        return ApiResponse.<UserResponse, Void>builder().result(userService.createUser(userCreationRequest)).build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>, Void> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username32:" + authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>, Void>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping(value = {"/{userId}"})
    public ApiResponse<UserResponse, Void> getUser(@PathVariable("userId") Long id) {
        return ApiResponse.<UserResponse, Void>builder().result(userService.getUser(id)).build();
    }

    @PutMapping(value = {"/{userId}"})
    public ApiResponse<UserResponse, Void> updateUser(@PathVariable("userId") Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse, Void>builder().result(userService.updateUser(id, userUpdateRequest)).build();
    }

    @GetMapping(value = "/myInfo")
    public ApiResponse<UserResponse, Void> getMyInfo() {
        return ApiResponse.<UserResponse, Void>builder().result(userService.getMyInfo()).build();
    }
}


