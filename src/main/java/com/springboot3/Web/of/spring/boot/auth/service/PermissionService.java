package com.springboot3.Web.of.spring.boot.auth.service;

import com.springboot3.Web.of.spring.boot.auth.dto.model.PermissionDto;
import com.springboot3.Web.of.spring.boot.auth.entity.Permission;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PermissionService {
    public abstract PermissionDto createPermission(PermissionDto permissionDto);

    public abstract List<PermissionDto> getPermissions();

    public abstract void deletePermission(String code);
}
