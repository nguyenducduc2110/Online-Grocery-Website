package com.springboot3.Web.of.spring.boot.auth.service.impl;

import com.springboot3.Web.of.spring.boot.auth.dto.mapper.RoleMapper;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.Permission;
import com.springboot3.Web.of.spring.boot.auth.exception.ErrorDetails;
import com.springboot3.Web.of.spring.boot.auth.exception.Springboot3Exception;
import com.springboot3.Web.of.spring.boot.auth.repository.PermissionRepository;
import com.springboot3.Web.of.spring.boot.auth.repository.RoleRepository;
import com.springboot3.Web.of.spring.boot.auth.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
//@AllArgsConstructor
@Log4j
@RequiredArgsConstructor //Có hiệu xuất tốt hơn
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @Override
    public RoleResponse create(RoleRequest roleDto) {
        var role = roleMapper.toRole(roleDto);
        //Map từ chuỗi Id sang object = hand
        var permissions = permissionRepository.findAllById(roleDto.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return  roleMapper.toRoleDto(roleRepository.save(role));
    }
    @Override
    public List<RoleResponse> getAll(){
        return roleRepository.findAll().stream().
                map(role -> roleMapper.toRoleDto(role)).toList();
    }
    //mấy cái role hay permission biểu trưng cho quyền thì chỉ nên truyền Id chứ ko để lộ name hay code để bảo mật
    //Để tránh nhầm nữa mà óc. User có nhiều role USER mà.
    @Override
    public void delete(String code) {
        roleRepository.deleteById(code);
    }
}
