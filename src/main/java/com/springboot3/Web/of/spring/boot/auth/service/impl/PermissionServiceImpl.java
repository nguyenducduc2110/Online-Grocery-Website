package com.springboot3.Web.of.spring.boot.auth.service.impl;

import com.springboot3.Web.of.spring.boot.auth.dto.mapper.PermissionMapper;
import com.springboot3.Web.of.spring.boot.auth.dto.model.PermissionDto;
import com.springboot3.Web.of.spring.boot.auth.entity.Permission;
import com.springboot3.Web.of.spring.boot.auth.repository.PermissionRepository;
import com.springboot3.Web.of.spring.boot.auth.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionDto createPermission(PermissionDto permissionDto) {
        Permission permission = permissionMapper.toPermission(permissionDto);
        return permissionMapper.toPermissionDto(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionDto> getPermissions() {
        return permissionRepository.findAll().stream().map(permission -> permissionMapper.toPermissionDto(permission)).toList();
    }

    @Override
    public void deletePermission(String code) {
        permissionRepository.deleteById(code);
    }
}
