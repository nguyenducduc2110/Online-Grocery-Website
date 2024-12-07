package com.springboot3.Web.of.spring.boot.auth.repository;

import com.springboot3.Web.of.spring.boot.auth.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
