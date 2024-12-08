package com.springboot3.Web.of.spring.boot.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.exception.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(ErrorDetails.UNAUTHORIZED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse apiResponse = ApiResponse.builder()
                .code(ErrorDetails.UNAUTHORIZED.getCode())
                .messages(ErrorDetails.UNAUTHORIZED.getMessage())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().print(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
