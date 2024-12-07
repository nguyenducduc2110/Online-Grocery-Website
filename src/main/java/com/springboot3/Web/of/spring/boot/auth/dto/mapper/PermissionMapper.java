package com.springboot3.Web.of.spring.boot.auth.dto.mapper;

import com.springboot3.Web.of.spring.boot.auth.dto.model.PermissionDto;
import com.springboot3.Web.of.spring.boot.auth.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionDto permissionDto);
    PermissionDto toPermissionDto(Permission permission);
}
