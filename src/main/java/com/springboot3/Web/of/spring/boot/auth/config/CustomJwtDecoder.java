package com.springboot3.Web.of.spring.boot.auth.config;

import com.nimbusds.jose.JOSEException;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.IntrospectRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.IntrospectResponse;
import com.springboot3.Web.of.spring.boot.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String SECRET;
    private final AuthService authService;
    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            IntrospectResponse introspectResponse =
                    authService.introspect(
                            IntrospectRequest.builder()
                                    .token(token)
                                    .build());
            if (!introspectResponse.isValid()) throw new JwtException("Invalid token");
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (nimbusJwtDecoder == null) {
            synchronized (this) {
                if (nimbusJwtDecoder == null) {
                    SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), "HS512");
                    nimbusJwtDecoder = NimbusJwtDecoder
                            .withSecretKey(secretKeySpec)
                            .macAlgorithm(MacAlgorithm.HS512)
                            .build();
                }
            }
        }
        return nimbusJwtDecoder.decode(token);
    }
}
