package com.springboot3.Web.of.spring.boot.auth.repository;

import com.springboot3.Web.of.spring.boot.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}