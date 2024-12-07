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
@RequiredArgsConstructor //Có hiệu xuất tốt hơn
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)//Final giúp đảm bảo tính bất biến
public class AuthServiceImpl implements AuthService {
    @NonFinal//Dùng để ko cho attribute có trong bean constructer required
    @Value("${jwt.signerKey}")
    protected String SECRET;
    @NonFinal
    @Value("${jwt.valid-duration}")//thời gian + thêm để gia hạn cho access token tính từ token lúc đc issue
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
            //Springboot3Exception cũng là 1 class tạo object nhưng khi object của class này đc ném
            //ra thì chính là object exception, chứa các thông báo lỗi
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(PredefindedRole.USER_ROLE).orElseThrow(() -> {throw new Springboot3Exception(ErrorDetails.NOT_FOUND);}));
        user.setRoles(roles);
        try {
            userRepository.save(user);
            //ngoại lệ này ktra nếu data truyền vào vi phạm ko tuân theo fk, unique, null.
        } catch (DataIntegrityViolationException e) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        return "Registered successfully!";
    }

//    VS Jwt Tức là ko cần lưu thông tin user vào userDetail khi chỉ login và dùng dữ liệu trong jwt, Còn khi muốn thao tác với dữ
//    liệu trong database của mỗi người dùng thì cần phải lưu user vào userDetail.-->Nên lưu vào userDetail vì trong UserDetails
//    kiểm tra:"tài khoản bị khóa", "tài khoản hết hạn",...
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        //matches dùng để so sánh mk bằng cách lấy salt của mk trong csdl + mk nhập và encode rồi so sánh
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
//        //Lưu user vào sys.
//        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//
//        // 3. Tạo đối tượng Authentication
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//        // 4. Lưu Authentication vào SecurityContextHolder
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //nếu đúng thì reponse ra token.
        return AuthenticationResponse.builder()
                .token(generateToken(user))
                //Nếu logic chạy vào đc đoạn này mà ko throw là đúng nên luôn - true.
                .authenticated(true)
                .build();
    }

    //Dùng để test tính hợp lệ của token và trả về result in object IntrospectResponse
    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
//        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
//        SignedJWT signedJWT = SignedJWT.parse(token);
//        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        boolean verified = signedJWT.verify(verifier);
         //Nếu token of client : token of server thì ném ra exception
        boolean inValid = true;
        //dùng try catch đoạn này để bắt exception của verifyToken để handle luôn
        //ko muốn ném vào Global để handle.
        //Ở dưới đây handle đơn giản chỉ trả về là false.
        try {
            verifyToken(token, false);
        }catch (Springboot3Exception ex){
            inValid = false;
        }
        return IntrospectResponse.builder()
                //Nếu expiryTime xảy ra sau new Date() trả về true
                .valid(inValid)
            .build();
    }

    private String generateToken(User user) {
        //header cho Signature(JWS có chữ S)
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                //dùng để verifyToken
                .keyID("auth-key")
                .contentType("JWT")
                //.criticalParams(new HashSet<>(Arrays.asList("exp", "nbf")))
                .build();
        //payload
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("DucNguyen")
                .claim("scope", buildScope(user))//set role cho jwt
                //Ko cần role vào token vì khi login có userDetails in sys. VìSpringSecurity sẽ dùng maches ktra quyền access của role in userDetail do login = token
                .issueTime(new Date())
                //Lấy time now +(plus)  giờ, xong chuyển đổi thành mili giây dạng số nguyên 1732358551(toEpochMilli), cuối cùng chuyển đổi thành object Date java
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                //định danh duy nhất cho token này. Nó giúp ngăn chặn việc sử dụng lại cùng một token nhiều lần.
                .jwtID(UUID.randomUUID().toString())
                //CÁCH DÙNG jwtID:
                //Khi server nhận được JWT trong một yêu cầu, nó có thể kiểm tra jwtID để xem liệu token đó đã được sử dụng hoặc có trong danh sách đen hay chưa:
//                String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
//
//                // Kiểm tra trong danh sách đen hoặc danh sách đã sử dụng
//                if (blacklist.contains(jwtID)) {
//                    throw new RuntimeException("Token đã bị vô hiệu hóa");
//                }
                //.claim("scope", bui)
                .build();
        //Chuyển các clainms thành JSON làm payload
        Payload payload = new Payload(claimsSet.toJSONObject());
        //Signature(tạo ra chữ ký từ header, payload, secret)
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            //Dùng jwsObject để ký tạo ra token
            //và SECRET cho vào token để nếu SECRET of client = SECRET of server thì cho phép extract
            //MACSigner là object nhận secret và kiểm tra tính toàn vẹn của secret và cung cấp secret cho jwsObject tạo ra Signature
            jwsObject.sign(new MACSigner(SECRET.getBytes()));
        } catch (JOSEException e) {
            log.error("Can not create token", e);
            throw new RuntimeException(e);
        }

        //tạo ra token từ Signature, header, payload
        return jwsObject.serialize();
    }

    //Lấy các role của user và nối nó thành 1 chuỗi cách nhau = space theo quy ước lấy role của oauth2ResourceServer
    private String buildScope(User user) {
        //khi decode token = config oauth2ResourceServer thì scopse của nó có conversion các role cách nhau = space nên dùng class này
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                scopeJoiner.add("ROLE_"+role.getCode());
                //Tại mỗi role thì nối luôn Permissions vs role đó
                //ADMIN UPDATE_POST APPROVE_POST REJECT_POST
                if(!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {scopeJoiner.add(permission.getCode());});
                }
            });

        }
        return  scopeJoiner.toString();
    }

    //khi logout xác minh token ở client đúng ko và chưa hết time thì lưu token đúng đó vào csdl
    @Override
    public Void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        //Lúc logout truyền true vào token muốn logout qua đc verify và save vào blacklist
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

    //Do method introspect và logout đều cần xác minh token nên tạo ra methdo này
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        //kiểm tra tính toàn vẹn(ko bị sửa) của token và secret token of client
        SignedJWT signedJWT = SignedJWT.parse(token);
//Ktra tính hợp lệ của chữ ký JWT như đúng thuật toán, config,.... token of server.
        JWSVerifier verifier = new MACVerifier(SECRET);
//So sánh token of client : token of server
        boolean verified = signedJWT.verify(verifier);
        //Đoạn này chính là refresh token thoe kiểu Oauth2
        //Đây chính là cách verify cho cả access token và refresh token
        //bằng cách chỉ cần 1 flag isRefresh là phân biệt đâu là verify cho access token và refresh token
        //Do method trả ra jwt sau khi verifyToken thì khi truyền true chính là cấp phát thêm thời gian cho token cũ làm refreshToken và trả ra refreshToken
        //===>Cung cấp thời gian cho token cũ khi hết hạn để token cũ đó làm refresh token và trong khi refresh token còn time(REFRESHABLE_DURATION) thì có phép cấp
        //phát token mới.
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        //token ko đúng và thời gian hết hạn sau tgian hiện tại
        if(!(verified && expiryTime.after(new Date()))) {
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
        //Kiểm tra nếu jwt có trong csdl là token đã logout nhưng vẫn còn time
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new Springboot3Exception(ErrorDetails.UNAUTHORIZED);
        }
        //signedJWT là jwt đã đc xác minh. Trả về jwt đc xác minh để khi logout lấy jwtId và lưu vào csdl
        return signedJWT;
    }

    @Override
    //Tgian tính toán token sắp expiry là ở FE. Khi nào token sắp hết hạn thì gọi endpoint vào method này
    public RefreshResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
        //Đoạn này chính là refresh token thoe kiểu Oauth2
        //Đây chính là cách verify cho cả access token và refresh token
        //bằng cách chỉ cần 1 flag isRefresh là phân biệt đâu là verify cho access token và refresh token
        //Do method trả ra jwt sau khi verifyToken thì khi truyền true chính là cấp phát thêm thời gian cho token cũ làm refreshToken và trả ra refreshToken
        //===>Cung cấp thời gian cho token cũ khi hết hạn để token cũ đó làm refresh token và trong khi refresh token còn time(REFRESHABLE_DURATION) thì có phép cấp
        //phát token mới.
        //===>Cung cấp thời gian cho token cũ khi hết hạn để token cũ đó làm refresh token và trong khi refresh token còn time(REFRESHABLE_DURATION) thì có phép cấp
        //phát token mới.
        SignedJWT signedJWT = verifyToken(refreshRequest.getToken(), true);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        //save refreshToken vào(Token cũ) vào save và lấy user trong token cũ tạo ra access token mới
        //Điều này tránh lộ access token mới
        invalidatedTokenRepository.save(invalidatedToken);
        //Khi user đã có token thì tức là đã login lấy user từ token đó và tạo token mới
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return RefreshResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }
}
