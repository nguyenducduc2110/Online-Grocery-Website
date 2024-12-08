package com.springboot3.Web.of.spring.boot.auth.dto.model.response;

import com.springboot3.Web.of.spring.boot.auth.dto.model.PermissionDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String name;
    String code;
    Set<PermissionDto> permissions;
}
