package com.springboot3.Web.of.spring.boot.auth.controller;

import com.springboot3.Web.of.spring.boot.auth.dto.model.PermissionDto;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping( "/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;


    @PostMapping
    public ApiResponse<PermissionDto, Void> createPermission(@RequestBody @Valid PermissionDto permissionDto) {
        return ApiResponse.<PermissionDto, Void>builder()
                .result(permissionService.createPermission(permissionDto))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionDto>, String> getPermissions() {
        return ApiResponse.<List<PermissionDto>, String>builder()
                .result(permissionService.getPermissions())
                .build();
    }

    @DeleteMapping("/{permissionCode}")
    public ApiResponse<Void, Void> deletePermission(@PathVariable("permissionCode") String code) {
        permissionService.deletePermission(code);
        return ApiResponse.<Void, Void>builder().build();
    }
}
