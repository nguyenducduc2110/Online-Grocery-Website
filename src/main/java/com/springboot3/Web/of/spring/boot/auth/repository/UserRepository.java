package com.springboot3.Web.of.spring.boot.auth.repository;

import com.springboot3.Web.of.spring.boot.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    public abstract Boolean existsByUsername(String username);
    List<User> findAll();
    //Dùng Option khi tìm 1 object nào đó để sử dụng method orElseThrow() ném ra exception cho Module hanlder exception chung hadnle
    //Để bắt buộc ném ra ngoại lệ
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
}
