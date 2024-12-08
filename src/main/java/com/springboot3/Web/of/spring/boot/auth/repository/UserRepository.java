package com.springboot3.Web.of.spring.boot.auth.repository;

import com.springboot3.Web.of.spring.boot.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    public abstract Boolean existsByUsername(String username);

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);
}
