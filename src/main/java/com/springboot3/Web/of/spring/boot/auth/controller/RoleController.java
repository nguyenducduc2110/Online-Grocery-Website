package com.springboot3.Web.of.spring.boot.auth.controller;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping( "/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse, Void> createRole(@RequestBody @Valid RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse, Void>builder()
                .result(roleService.create(roleRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>, Void> getRoles() {
        return ApiResponse.<List<RoleResponse>, Void>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{roleCode}")
    public ApiResponse<Void, Void> deleteRole(@PathVariable("roleCode") String code) {
        roleService.delete(code);
        return ApiResponse.<Void, Void>builder().build();
    }
}
