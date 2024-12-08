package com.springboot3.Web.of.spring.boot.auth.service;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;

import java.util.HashSet;
import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest roleDto);

    List<RoleResponse> getAll();

    void delete(String code);
}