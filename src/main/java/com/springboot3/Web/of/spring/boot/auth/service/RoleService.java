package com.springboot3.Web.of.spring.boot.auth.service;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RoleRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RoleResponse;

import java.util.HashSet;
import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest roleDto);

    List<RoleResponse> getAll();

    //mấy cái role hay permission biểu trưng cho quyền thì chỉ nên truyền Id chứ ko để lộ name hay code để bảo mật
    //Để tránh nhầm nữa mà óc. User có nhiều role USER mà.
    void delete(String code);
}