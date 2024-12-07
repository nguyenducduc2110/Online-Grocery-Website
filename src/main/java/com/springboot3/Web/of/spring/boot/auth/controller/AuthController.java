package com.springboot3.Web.of.spring.boot.auth.controller;

import com.nimbusds.jose.JOSEException;
import com.springboot3.Web.of.spring.boot.auth.dto.model.RegisterDto;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.AuthenticationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.IntrospectRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.LogoutRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RefreshRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.AuthenticationResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.IntrospectResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RefreshResponse;
import com.springboot3.Web.of.spring.boot.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")//Lý do nên dùng enpoint chung ntn: Để trong spring security chỉ cần khai báo endpoint auth** là dùng đc all các subEndpoint mà ko cần phải khai báo
@RequiredArgsConstructor
@Log4j
public class AuthController {
    private final AuthService authService;
    @PostMapping(value = {"/register", "/signup"})
    public ApiResponse<String, Void> register(@RequestBody @Valid RegisterDto registerDto) {
        log.info("AuthController:Đăng ký xong");
        return  ApiResponse.<String, Void>builder().result(authService.register(registerDto)).build();
    }

    //Login và trả ra token
    @PostMapping(value = {"/token"})
    public ApiResponse<AuthenticationResponse, Void> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        log.info("AuthController:Authenticate");
        //trc method builer có generic vì nó có type generic:
        //public static <T, M> ApiResponse.ApiResponseBuilder<T, M> builder()
        return ApiResponse.<AuthenticationResponse, Void>builder().result(authService.authenticate(authenticationRequest)).build();
    }

    //Authenticate token
    @PostMapping(value = {"/introspect"})
    public ApiResponse<IntrospectResponse, String> authenticate(@RequestBody @Valid IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse, String>builder().result( authService.introspect(introspectRequest)).build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void, Void> logout(@RequestBody @Valid LogoutRequest logoutRequest) throws ParseException, JOSEException {
        return ApiResponse.<Void, Void>builder().result(authService.logout(logoutRequest)).build();
    }

    @PostMapping(value = {"/refresh"})
    public ApiResponse<RefreshResponse, Void> refreshToken(@RequestBody @Valid RefreshRequest refreshRequest) throws ParseException, JOSEException {
        return ApiResponse.<RefreshResponse, Void>builder().result(authService.refreshToken(refreshRequest)).build();
    }
}
