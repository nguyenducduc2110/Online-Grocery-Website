package com.springboot3.Web.of.spring.boot.auth.dto.mapper;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    //Đoạn code này chỉ bỏ qua của method toRole thôi vì đầu vào list permissions là Set<String> nên ko thể map từ String thành object đc
    @Mapping(target = "permissions", ignore = true)
    //RoleRequest != RoleResponse ở việc thuộc tính của RoleRequest là 1 list Id permission. Còn RoleResponse là 1 list object
    //Do ko cùng type nên phải ánh xạ = hand
    Role toRole(RoleRequest roleDto);
    RoleResponse toRoleDto(Role role);
}
