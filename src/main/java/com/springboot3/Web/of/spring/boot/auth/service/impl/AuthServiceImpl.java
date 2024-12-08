package com.springboot3.Web.of.spring.boot.auth.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.springboot3.Web.of.spring.boot.auth.contant.PredefindedRole;
import com.springboot3.Web.of.spring.boot.auth.dto.model.RegisterDto;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.AuthenticationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.IntrospectRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.LogoutRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.RefreshRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.AuthenticationResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.IntrospectResponse;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.RefreshResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.InvalidatedToken;
import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import com.springboot3.Web.of.spring.boot.auth.exception.ErrorDetails;
import com.springboot3.Web.of.spring.boot.auth.exception.Springboot3Exception;
import com.springboot3.Web.of.spring.boot.auth.repository.InvalidatedTokenRepository;
import com.springboot3.Web.of.spring.boot.auth.repository.RoleRepository;
import com.springboot3.Web.of.spring.boot.auth.repository.UserRepository;
import com.springboot3.Web.of.spring.boot.auth.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Log4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SECRET;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;


    UserRepository userRepository;
    RoleRepository roleRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public String register(RegisterDto registerDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (this.userRepository.existsByUsername(registerDto.getUsername())) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(PredefindedRole.USER_ROLE).orElseThrow(() -> {
            throw new Springboot3Exception(ErrorDetails.NOT_FOUND);
        }));
        user.setRoles(roles);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        return "Registered successfully!";
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean inValid = true;
        try {
            verifyToken(token, false);
        } catch (Springboot3Exception ex) {
            inValid = false;
        }
        return IntrospectResponse.builder()
                .valid(inValid)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .keyID("auth-key")
                .contentType("JWT")
                .build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("DucNguyen")
                .claim("scope", buildScope(user)).issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SECRET.getBytes()));
        } catch (JOSEException e) {
            log.error("Can not create token", e);
            throw new RuntimeException(e);
        }

        return jwsObject.serialize();
    }

    private String buildScope(User user) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                scopeJoiner.add("ROLE_" + role.getCode());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        scopeJoiner.add(permission.getCode());
                    });
                }
            });

        }
        return scopeJoiner.toString();
    }

    @Override
    public Void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(logoutRequest.getToken(), true);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        return null;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET);
        boolean verified = signedJWT.verify(verifier);
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(verified && expiryTime.after(new Date()))) {
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
        return signedJWT;
    }

    @Override
    public RefreshResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshRequest.getToken(), true);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return RefreshResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }
}
