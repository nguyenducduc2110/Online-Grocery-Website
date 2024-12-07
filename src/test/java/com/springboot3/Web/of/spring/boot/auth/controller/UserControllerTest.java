package com.springboot3.Web.of.spring.boot.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.service.UserService;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import com.springboot3.Web.of.spring.boot.auth.repository.UserRepository;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;


//==>Đây là 1 testcase của UserController
@Log4j
@AutoConfigureMockMvc
//Dùng để sử dụng H2 Database cho test-case này để ko phải sử dụng databse mysql ở môi trường ngoài giúp test isolate(độc lập)
//Lý do trong H2 Database ko có structure table mà vẫn tương tác đc là do test.properties overide lại table trong config table in mysql of application.yml
@TestPropertySource("/test.properties")
@SpringBootTest//Chỉ định bean config của 1 module test, để lấy toàn bộ bean muốn dùng trong spring container.
public class UserControllerTest {
    //Attribute này dùng để gửi request + data vào endpoint của method controller muốn test mà ko cần restart server
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private UserResponse userDto;

    private LocalDate dob;
    private UserResponse userDto1;
    @MockBean//cung cấp bean giả của userService(bằng cách nhân bản bean trong container) để dưới dùng
    //==>Thế thì phải chạy server để có bean trong container rồi nhân bản.Nếu ko thì phải có @MockBean đến all các attribute mà các class dùng attribute
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
        dob = LocalDate.of(1990, 1, 1);
        //request gửi, dữ liệu này truyền vào phải chuẩn đúng theo request annotation của UserDto
        userDto = new UserResponse();
        userDto.setUsername("duc2110");
        userDto.setFirstName("Nguyen");
        userDto.setLastName("Duc");
        userDto.setPassword("123456");
        userDto.setDob(dob);
        //Object userDto1  thiết lập bắt buộc số lượng ccác trường tương ứng phải trả về
        userDto1 = new UserResponse();
        userDto1.setId(1L);//TRường id trong userDto1 bắt buộc phải giống value trả về ở dưới expect ktra result.id bắt buộc trả về = 1.
        userDto1.setUsername("duc2110");
        userDto1.setFirstName("Nguyen");
        userDto1.setLastName("Duc");
        userDto1.setDob(dob);
        //chỉ định user data trả về sau khi test
        user = User.builder()
                .id(1L)
                .username("duc2110")
                .firstName("Nguyen")
                .lastName("Duc")
                .build();
    }

    //===>Với method này code này có truy vấn tương tác với cơ sở dữ liệu nhưng nó chỉ mô phỏng cách get, post data thôi chứ ko đẩy data xuống csdl.
    @Test//Đánh dấu đây là 1 unit test
    //Nói chung khi viết test cho method của constroller thì config các attribute và data mà method cần để test method.
    public void createUser_validRequest_success() throws Exception {
        //GIVEN(đưa cho):là data đầu vào biết trc
        //ObjectMapper dùng để convert giữa Java và JSON
        ObjectMapper objectMapper = new ObjectMapper();
        //Đăng ký thêm module để có thể convert đc kiểu Time mà ObjectMapper ko convert đc
        objectMapper.registerModule(new JavaTimeModule());
        //convert object userDto thành JSON(Bình thường là JSON trên body vào Dto nhận và anntation lấy JSON in body và ánh xạ vào biến Dto)
        String content = objectMapper.writeValueAsString(userDto);
        //1---Ktra method createUser hoạt động đúng ko
        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userDto1);
        //2---Kiểm tra 1 request vào method có endpoint, với 1 request vào bean có endpoint "/users" của UserController có reponse đúng hay sai
        mockMvc.perform(MockMvcRequestBuilders//class desgin builder
                        .post("/users")//dùng http post
                        .contentType(MediaType.APPLICATION_JSON_VALUE)//kiểu data truyền là JSON
                        .content(content))//data phần của body
                //3---Test xem khi content vào bean có endpint "/users" có trả về status=200 và code = 1000
                .andExpect(MockMvcResultMatchers.status().isOk())//MockMvcResultMatchers: Class test state trả về khi request vào
                .andExpect(MockMvcResultMatchers.jsonPath("code")//jsonPath:Test xem trong response của body có fields code: 1000 thì test đúng
                        .value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id")
                        .value(1L)
                );
    }
    //===>Đoạn 2 sẽ phải dựa vào mô phòng Mockito của đoạn 1 và truyền .content(content) và xử lý theo mô phỏng đó và trả về đúng kết quả theo mô phỏng(Nên userDto1 phải reponse đúng value dự định mong muốn)


}