package com.springboot3.Web.of.spring.boot.auth.config;

import com.springboot3.Web.of.spring.boot.auth.contant.PredefindedRole;
import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import com.springboot3.Web.of.spring.boot.auth.repository.RoleRepository;
import com.springboot3.Web.of.spring.boot.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("ApplicationInitConfig: Initializing application.....");
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findById(PredefindedRole.ADMIN_ROLE).get());
                User user = User.builder()
                        .username("admin")
                        .firstName("admin")
                        .lastName("admin")
                        .password(passwordEncoder.encode("123456"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
            }
            log.info("ApplicationInitConfig: Application initialization completed .....");
        };
    }
}
