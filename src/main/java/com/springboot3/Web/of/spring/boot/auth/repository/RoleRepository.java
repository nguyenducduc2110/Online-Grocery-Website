package com.springboot3.Web.of.spring.boot.auth.repository;

import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

}
