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

//Tạo người dùng admin mặc định(quản trị ban đầu cho sys)  nếu chưa tồn tại trong cơ sở dữ liệu
//Việc yêu cầu đổi mật khẩu ngay sau khi khởi tạo giúp bảo vệ an ninh khi ứng dụng
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    //ApplicationRunner Dùng để chạy 1 đoạn logic ngay sau khi ứng dụng khởi động xong.
    //Khi chạy unit test nó sẽ khởi động lại toàn bộ context(container) của ứng dụng
    //và applicationRunner là 1 bean nên nó cũng sẽ khởi chạy lại. Nhưng do dùng H2 database(Mặc dù mô phỏng lại databse + entity của app)
    //nhưng trong H2 database ko có data "admin". Nên khi start unit test config này lỗi.
    @Bean
    //Anotaition này giúp chỉ định khi 1 thuộc tính config dc sử dụng trong hệ thống thì mới bean này mới hoạt động(block code mới hoạt động)
    @ConditionalOnProperty(prefix = "spring",
    value = "datasource.driverClassName",
    havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("ApplicationInitConfig: Initializing application.....");
        //args chính là userRepository viết = lamda và return ra ApplicationRunner
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findById(PredefindedRole.ADMIN_ROLE).get());
                //khi insert user kiểu này phải để cascade = CascadeType.MERGE insert table cha trc xong bảng con
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
