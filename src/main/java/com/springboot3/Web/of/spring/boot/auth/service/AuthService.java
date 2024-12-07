package com.springboot3.Web.of.spring.boot.auth.service;

import com.nimbusds.jose.JOSEException;
import com.springboot3.Web.of.spring.boot.auth.dto.model.RegisterDto;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.AuthenticationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.IntrospectRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.LogoutRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RefreshRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.AuthenticationResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.IntrospectResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RefreshResponse;

import java.text.ParseException;

public interface AuthService {
    public abstract String register(RegisterDto registerDto);

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    Void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException;

    RefreshResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException;
}
