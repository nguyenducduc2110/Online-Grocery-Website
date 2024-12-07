package com.springboot3.Web.of.spring.boot.auth.service;

import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import com.springboot3.Web.of.spring.boot.auth.repository.UserRepository;
import lombok.extern.log4j.Log4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;



//==>Đây là 1 testcase của UserController
@Log4j
@AutoConfigureMockMvc
//Dùng để sử dụng H2 Database cho test-case này để ko phải sử dụng databse mysql ở môi trường ngoài giúp test isolate(độc lập)
//Lý do trong H2 Database ko có structure table mà vẫn tương tác đc là do test.properties overide lại table trong config table in mysql of application.yml
//@TestPropertySource("/test.properties")
@SpringBootTest//Chỉ định bean config của 1 module test, để lấy toàn bộ bean muốn dùng trong spring container.
public class UserServiceTest {
    //Attribute này dùng để gửi request + data vào endpoint của method controller muốn test mà ko cần restart server
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private UserResponse userDto;

    private UserResponse userDto1;

    //==>Thế thì phải chạy server để có bean trong container rồi nhân bản.Nếu ko thì phải có @MockBean đến all các attribute mà các class dùng attribute
    @MockBean//cung cấp bean giả của userService(bằng cách nhân bản bean trong container) để dưới dùng
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    //OR:
    //Đây cũng là 1 mock Oject để gọi method tạo luôn ko phải chạy server để nhân bản bean trong spring container.(Nhưng phải tạo nhiều MOck Obejct là các thuộc tính cần dùng)
    //private  UserService userService = Mockito.mock(UserService.class);

    //Dùng để setup data cho method muốn test
    //Method có @BeforeEach thì luôn đc chạy trc method có @Test để đảm bảo có data
    @BeforeEach
    void initData(){
        //request gửi, dữ liệu này truyền vào phải chuẩn đúng theo request annotation của UserDto
        userDto = new UserResponse();
        userDto.setUsername("fasdfdfadadfa");
        userDto.setFirstName("Nguyen");
        userDto.setLastName("Duc");
        userDto.setPassword("123456");
        //Object userDto1  thiết lập bắt buộc số lượng ccác trường tương ứng phải trả về
        userDto1 = new UserResponse();
        userDto1.setId(1L);//TRường id trong userDto1 bắt buộc phải giống value trả về ở dưới expect ktra result.id bắt buộc trả về = 1.
        userDto1.setUsername("fasdfdfadadfa");
        userDto1.setFirstName("Nguyen");
        userDto1.setLastName("Duc");
        //chỉ định user data trả về sau khi test
        user = User.builder()
                .id(1L)
                .username("fasdfdfadadfa")
                .firstName("Nguyen")
                .lastName("Duc")
                .build();
    }

    //==>Lý do ko test giống controller vì controller là test  mô phỏng các yêu cầu HTTP và xác thực phản hồi. Còn service là test method và xác thực reponse
    //===>Với method này code này có truy vấn tương tác với cơ sở dữ liệu nhưng nó chỉ mô phỏng cách get, post data thôi chứ ko đẩy data xuống csdl.
    @Test
    @WithMockUser(username = "fasdfdfadadfa")//Dùng để tạo mock Authentication chứa user trong sys
    void getMyInfo_valid_success() throws Exception {
        //Do method findByUsername trả về Optional<User> nên phải truyền nó vào Optional<User>
        //NẾu có đoạn code này gây lỗi vì nó cung cấp behavior mock của userRepository làm cho userService.getMyInfo(); làm theo  behavior của mock của userRepository gây trả về sai result
        //Mockito.when(userRepository.findByUsername("fasdfdfadadfa")).thenReturn(Optional.of(user));
        Mockito.when(userService.getMyInfo()).thenReturn(userDto1);
        var userDto = userService.getMyInfo();
        //Nếu username of userDTO trả về  = valid chỉ định thì qua ko thì throw exception lỗi
        Assertions.assertThat(userDto.getUsername()).isEqualTo("fasdfdfadadfa");
        Assertions.assertThat(userDto.getId()).isEqualTo(1L);

    }

}
