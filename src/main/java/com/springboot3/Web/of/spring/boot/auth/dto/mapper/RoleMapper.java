package com.springboot3.Web.of.spring.boot.auth.dto.mapper;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleDto);

    RoleResponse toRoleDto(Role role);
}
