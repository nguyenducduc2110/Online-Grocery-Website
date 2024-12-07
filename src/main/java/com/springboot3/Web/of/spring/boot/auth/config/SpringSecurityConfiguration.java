package com.springboot3.Web.of.spring.boot.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//VỚi config SpringSecurity cứ nhìn vào ảnh mô hình architecture Spring Security để config theo mô hình đó
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//===>Điều kiện muốn 1 class dùng đc bean constructer này thì class đó cũng phải nằm trong spring container.
//Nên class muốn dùng cũng để annotain các annotaion:@Service, @Component, @Repository, @Controller,@RestController, @Configuration   +   @AllArgsConstructor, @RequiredArgsConstructor
@EnableMethodSecurity//Chỉ định cho phép config security phân quyền cho từng method.
public class SpringSecurityConfiguration {
    private final CustomJwtDecoder customJwtDecoder;
    //Các endpoint public này sẽ ko bị chặn bởi
    private final String[] PUBLIC_ENDPOINTS = {"/auth/**", "/users/**"};
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Nếu ko tắt cái này thì postman ko gửi đc request
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                    authorizeRequests
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll().
                            requestMatchers(HttpMethod.GET, "/users").permitAll()
                            //phải login thì mới lấy đc user trong sys và trả về Info
                            .requestMatchers(HttpMethod.GET, "/users/myInfo").authenticated()
                            //Muốn truy cập vào endpoint thì jwt phải có role USER
                            //Do khi set Role cho cliam ở generate token thì lúc token vào đây oauth2 nó sẽ decode và map thành GrantedAuthority cho oauth2 phân quyền truy cập.
                            //Nên nó map ADMIN thành SCOPE_ADMIN
//                           .requestMatchers(HttpMethod.GET, "/users").hasRole(PredefindedRole.ADMIN_ROLE)
//                            .requestMatchers(HttpMethod.PUT, "/users/{userId}").hasRole(PredefindedRole.ADMIN_ROLE)
                        .anyRequest().authenticated()
                )
//                -Cấu hình này là filter yêu cầu mỗi yêu cầu HTTP phải chứa một token(trừ endpoint public) hợp lệ trong header. Token sẽ được giải mã và lưu info user vừa decode và save vào SecurityContext.
//                -Nếu một yêu cầu không có JWT thì Spring Security sẽ chặn yêu cầu đó và trả về lỗi 401 Unauthorized.
//===>Config này ko có hiệu lực với các endpoint public.Còn các endpoint remaining bắt buộc phải có token
                //==>config này là filter lọc jwt trong header của request và lấy Role trong jwt để kiểm tra quyền truy cập endpoint của request
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer ->
                                //===>Và method introspect đc sử dụng ở oauth2ResourceServer trong lúc decode
                                        // token(signedJWT ko phải tự nhiên decode và lấy đc claim mà là do decode
                                        // của chuỗi filter khi request vào SpringSecurity) và introspect luôn
                                        // .Nếu token ko hợp lệ và token đã từng đc use(jwtId đã lưu vào csdl) thì ném ra ngoại lệ chưa xác thực.
                                        jwtConfigurer.decoder(customJwtDecoder)
                                //handle exception 401 Unauthorized (ko có role or sai JWT)
  //===>VS all các exception nó đều ném vào bean này(exception configuration) để hanlde.Riêng 401 Unauthorized JWT KO CÓ VALUE field (Scopse) or JWT KO ĐÚNG. thì nó ném exception
//đó vào Spring Security(Nên phải config handle exception) ở Security.
//===>Chỉ cần lên jwt.io SỬA VỢI role cũng lỗi. VD: "scope": "ROLE_ADMIN ROLE_USER"->"ROLE_ADMIN" mặc dù method PreAuthorized:ROLE_ADMIN ->Vẫn trả về lỗi này
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))//Convert Jwt thành authentication để save trong sys
//          ===>Khi nào nên dùng bean và khi nào ko:
//        -Dùng khi nhiều method trong 1 class sẽ dùng nhiều object bean để để lấy method trong bean.
//        Còn nếu trong 1 class object đó chỉ dùng 1 lần THÌ CLASS ĐC GỌI CŨNG KO CẦN CONFIG BEAN.
                                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
                //Ko cần endpoint /login vẫn có url /login khi để formlogin là default
                .formLogin(Customizer.withDefaults());
        //http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    // để chuyển đổi JWT thành một Authentication có chứa quyền GrantedAuthority
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        //chuyển đổi các quyền trong JWT (như scope hoặc roles) thành các quyền mà Spring Security sử dụng
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        //jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");//Từ SCOPSE_ADMIN -> ROLE_ADMIN
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");//Vì config Scopse ở AuthSerivce đã tự thêm prefix để phân biệt role và permission
        //Đặt JwtGrantedAuthoritiesConverter vào JwtAuthenticationConverter.
        //Điều này đảm bảo rằng JwtAuthenticationConverter sẽ sử dụng jwtGrantedAuthoritiesConverter để chuyển đổi quyền từ JWT thành các GrantedAuthority.
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    @Bean//=====>Bean này sẽ đưa object vào container cho class khác dùng và chính class này dùng.Nhưng class đó cũng trong spring container mới dùng đc bean
    public PasswordEncoder passwordEncoder( ) {
        return new BCryptPasswordEncoder(10);
    }

    //JwtDecoder Giúp giải mã và xác thực JWT
    //method này trả về object JwtDecoder cho  oauth2ResourceServer để decode.
//    @Bean
//    public JwtDecoder jwtDecoder(){
//        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), "HS512");
//        //Trả về 1 objcet thuật toán mã hóa cho token của client với SECRET có sẵn
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    //Config này login là vs formLogin default và save user vào sys
    //Ở AuthController cũng cần save user vừa login vào trong sys
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
//    //AuthenticationManager quản lý Lưu trữ SecurityContext(Lại chứa userDetail)
//    //làm attribute bean ở mọi nơi để lưu user vào sys
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins("http://localhost:3001") // Allow your frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowCredentials(true); // Allow credentials (if needed)
            }
        };
    }
}
