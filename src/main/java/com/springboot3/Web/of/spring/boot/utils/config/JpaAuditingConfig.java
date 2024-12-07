package com.springboot3.Web.of.spring.boot.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
    @Bean
    AuditorAware<String> auditorAware() {//Cấu hình này nó sẽ cung cấp createDate, modifiedDate, createBy
        return new AuditorAwareImpl();
    }
    //Đây là cung cấp config cho AuditorAware cho field modifiedBy
    private static class AuditorAwareImpl implements   AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            return Optional.of(authentication.getName());
        }
    }
}
