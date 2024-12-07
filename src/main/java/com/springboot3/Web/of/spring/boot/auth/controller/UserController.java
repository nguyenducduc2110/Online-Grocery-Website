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
//Lý do nên dùng enpoint chung ntn: Để trong spring security chỉ cần khai báo endpoint auth** là dùng đc all các subEndpoint mà ko cần phải khai báo
@RequestMapping("/users")//Làm endpoint chung cho all method(method chỉ cần truyền id + kiểu post, get,..)
public class UserController {
    private final UserService userService;

    @PostMapping//Ko cần value endpoint vì cứ post và data ở postman là gọi đc method này.Ko nhầm User vs Order vì có RequestMapping map endponit chung kia rồi
    //attribute message có kiểu Void ko đc gán = null. Mà field = null thì Jackson tự động loại bỏ field đó khi convert to JSON.
    public ApiResponse<UserResponse, Void> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
       log.info("Controller:Create User");

        return ApiResponse.<UserResponse, Void>builder().result(userService.createUser(userCreationRequest)).build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>, Void> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username32:"+ authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>, Void>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping(value = {"/{userId}"})
    //Do reponse user(đa số việc get data) ko cần trả về status
    //Nên ko cần ResponseEntity mà dùng ApiResponse để chuẩn hóa response chung
    public ApiResponse<UserResponse, Void> getUser(@PathVariable("userId") Long id) {
        return ApiResponse.<UserResponse, Void>builder().result(userService.getUser(id)).build();
    }
    //Update cũng dùng chung endpoint
    // nhưng annotation put khác get thì fetch, postman cũng dùng HttpMethod put, get khác nên nó sẽ tìm đúng endpoint.
    @PutMapping(value = {"/{userId}"})
    public ApiResponse<UserResponse, Void> updateUser(@PathVariable("userId") Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse, Void>builder().result(userService.updateUser(id, userUpdateRequest)).build();
    }
    @GetMapping(value = "/myInfo")
    public ApiResponse<UserResponse, Void> getMyInfo(){
        return ApiResponse.<UserResponse, Void>builder().result(userService.getMyInfo()).build();
    }
}


