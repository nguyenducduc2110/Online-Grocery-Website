//package com.springboot3.Web.of.spring.boot.auth.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
//import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
//import com.springboot3.Web.of.spring.boot.auth.entity.User;
//import com.springboot3.Web.of.spring.boot.auth.service.UserService;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//
//
////==>Đây là 1 testcase của UserController
//@Log4j2
//
//@AutoConfigureMockMvc
//@SpringBootTest//Chỉ định bean config của 1 module test, để lấy toàn bộ bean muốn dùng trong spring container.
//@Testcontainers//cô lập và cung cấp môi trường Docker containers cho rieng class test này
//public class UserControllerIntegrationTest {
//    //Attribute này dùng để gửi request + data vào endpoint của method controller muốn test mà ko cần restart server
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    //Đoạn code này là tạo ra 1 MySQLContainer có tên mysql:latest trong Docker cho việc test
//    //Tạo ra 2 container: môi trường test và MySQLContainer để interative vs csdl
//    @Container
//    static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest");
//
//    //Đoạn code này là cung cấp config mysql của MySQLContainer cho application.yml của spring test
//    @DynamicPropertySource
//    static void confgureDatasource(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
//        registry.add("spring.datasource.driver-class-name", ()->"com.mysql.cj.jdbc.Driver");
//        registry.add("spring.jpa.hibernate.ddl-auto", ()->"update");
//    }
//
//    private User user;
//    private UserCreationRequest userDto;
//    private LocalDate dob;
//    private UserResponse userDto1;
//
//    //OR:v
//    //Đây cũng là 1 mock Oject để gọi method tạo luôn ko phải chạy server để nhân bản bean trong spring container.(Nhưng phải tạo nhiều MOck Obejct là các thuộc tính cần dùng)
//    //private  UserService userService = Mockito.mock(UserService.class);
//
//    //Dùng để setup data cho method muốn test
//    //Method có @BeforeEach thì luôn đc chạy trc method có @Test để đảm bảo có data
//    @BeforeEach
//    void initData(){
//        dob = LocalDate.of(1990, 1, 1);
//        //request gửi, dữ liệu này truyền vào phải chuẩn đúng theo request annotation của UserDto
//        userDto = new UserCreationRequest();
//        userDto.setUsername("duc21102004");
//        userDto.setFirstName("Nguyen");
//        userDto.setLastName("Duc");
//        userDto.setPassword("123456");
//        userDto.setDob(dob);
//        //Object userDto1  thiết lập bắt buộc số lượng ccác trường tương ứng phải trả về
//        userDto1 = new UserResponse();
//        userDto1.setId(1L);//TRường id trong userDto1 bắt buộc phải giống value trả về ở dưới expect ktra result.id bắt buộc trả về = 1.
//        userDto1.setUsername("duc21102004");
//        userDto1.setFirstName("Nguyen");
//        userDto1.setLastName("Duc");
//        userDto1.setDob(dob);
//        userDto1.setPassword("$2a$10$AjeGTaWy0BHrer1/8TsCD.vtorxdtQ0Sg4Vmk2od.llIgQR8nT5XW");
//        //chỉ định user data trả về sau khi test
//        user = User.builder()
//                .id(1L)
//                .username("duc21102004")
//                .firstName("Nguyen")
//                .lastName("Duc")
//                .password("$2a$10$AjeGTaWy0BHrer1/8TsCD.vtorxdtQ0Sg4Vmk2od.llIgQR8nT5XW")
//                .build();
//    }
//
//    //===>Với method này code này có truy vấn tương tác với cơ sở dữ liệu nhưng nó chỉ mô phỏng cách get, post data thôi chứ ko đẩy data xuống csdl.
//    @Test//Đánh dấu đây là 1 unit test
//    //Nói chung khi viết test cho method của constroller thì config các attribute và data mà method cần để test method.
//    public void createUser_validRequest_success() throws Exception {
//        //GIVEN(đưa cho):là data đầu vào biết trc
//        //ObjectMapper dùng để convert giữa Java và JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        //Đăng ký thêm module để có thể convert đc kiểu Time mà ObjectMapper ko convert đc
//        objectMapper.registerModule(new JavaTimeModule());
//        //convert object userDto thành JSON(Bình thường là JSON trên body vào Dto nhận và anntation lấy JSON in body và ánh xạ vào biến Dto)
//        String content = objectMapper.writeValueAsString(userDto);
//        //1---Ktra method createUser hoạt động đúng ko
//
//        //2---Kiểm tra 1 request vào method có endpoint, với 1 request vào bean có endpoint "/users" của UserController có reponse đúng hay sai
//       var reponse =  mockMvc.perform(MockMvcRequestBuilders//class desgin builder
//                        .post("/users")//dùng http post
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)//kiểu data truyền là JSON
//                        .content(content))//data phần của body
//                //3---Test xem khi content vào bean có endpint "/users" có trả về status=200 và code = 1000
//                .andExpect(MockMvcResultMatchers.status().isOk())//MockMvcResultMatchers: Class test state trả về khi request vào
//                .andExpect(MockMvcResultMatchers.jsonPath("code")//jsonPath:Test xem trong response của body có fields code: 1000 thì test đúng
//                        .value(1000))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.username")
//                        .value("duc21102004"))
//                        .andExpect(MockMvcResultMatchers.jsonPath("result.firstName")
//                                .value("Nguyen"))
//                        .andExpect(MockMvcResultMatchers.jsonPath("result.lastName")
//                                .value("Duc"))
//               .andReturn();
//       log.info("UserControllerIntegrationTest result: "+reponse.getResponse().getContentAsString());
//    }
//    //===>Đoạn 2 sẽ phải dựa vào mô phòng Mockito của đoạn 1 và truyền .content(content) và xử lý theo mô phỏng đó và trả về đúng kết quả theo mô phỏng(Nên userDto1 phải reponse đúng value dự định mong muốn)
//}