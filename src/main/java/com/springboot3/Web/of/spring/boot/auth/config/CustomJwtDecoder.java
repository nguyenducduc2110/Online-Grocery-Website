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

//===>Và method introspect đc sử dụng ở oauth2ResourceServer trong lúc decode
// token(signedJWT ko phải tự nhiên decode và lấy đc claim mà là do decode
// của chuỗi filter khi request vào SpringSecurity) và introspect luôn
// .Nếu token ko hợp lệ và token đã từng đc use(jwtId đã lưu vào csdl) thì ném ra ngoại lệ chưa xác thực.
@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String SECRET;
    private final AuthService authService;//chỉ những
    //chỉ được khởi tạo một lần duy nhất trong suốt vòng đời của CustomJwtDecoder
    private NimbusJwtDecoder nimbusJwtDecoder ;
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            //Nếu token trên header vào mà đã có trong csdl
            IntrospectResponse introspectResponse =
                    authService.introspect(
                            IntrospectRequest.builder()
                                    .token(token)
                                    .build());
            if (!introspectResponse.isValid()) throw new JwtException("Invalid token");
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e.getMessage());
        }
        //chỉ được khởi tạo một lần duy nhất trong suốt vòng đời của CustomJwtDecoder
//        if (Objects.isNull(nimbusJwtDecoder)) {//NÊn xem lại singleton để sửa
//            //Nếu token hợp lệ secret in client = secret in server, token là mới nhất thì cho decode để lưu user vào sys
//
//            //chuyển secret thành byte
//            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), "HS512");
//            //Trả về 1 objcet thuật toán mã hóa cho token của client với SECRET có sẵn
//            nimbusJwtDecoder = NimbusJwtDecoder
//                    .withSecretKey(secretKeySpec)
//                    .macAlgorithm(MacAlgorithm.HS512)
//                    .build();
//        }
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
        //decode token và trả về Allclaims
        return nimbusJwtDecoder.decode(token);
    }
}
