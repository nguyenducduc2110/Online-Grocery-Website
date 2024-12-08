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


@Log4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@SpringBootTestpublic class UserControllerTest {
        @Autowired
    private MockMvc mockMvc;
    private User user;
    private UserResponse userDto;

    private LocalDate dob;
    private UserResponse userDto1;
    @MockBean        private UserService userService;
    @Autowired
    private UserRepository userRepository;
            //private  UserService userService = Mockito.mock(UserService.class);

            @BeforeEach
    void initData(){
        dob = LocalDate.of(1990, 1, 1);
                userDto = new UserResponse();
        userDto.setUsername("duc2110");
        userDto.setFirstName("Nguyen");
        userDto.setLastName("Duc");
        userDto.setPassword("123456");
        userDto.setDob(dob);
                userDto1 = new UserResponse();
        userDto1.setId(1L);        userDto1.setUsername("duc2110");
        userDto1.setFirstName("Nguyen");
        userDto1.setLastName("Duc");
        userDto1.setDob(dob);
                user = User.builder()
                .id(1L)
                .username("duc2110")
                .firstName("Nguyen")
                .lastName("Duc")
                .build();
    }

        @Test        public void createUser_validRequest_success() throws Exception {
                        ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                String content = objectMapper.writeValueAsString(userDto);
                Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userDto1);
                mockMvc.perform(MockMvcRequestBuilders                        .post("/users")                        .contentType(MediaType.APPLICATION_JSON_VALUE)                        .content(content))                                .andExpect(MockMvcResultMatchers.status().isOk())                .andExpect(MockMvcResultMatchers.jsonPath("code")                        .value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id")
                        .value(1L)
                );
    }
    //===>Đoạn 2 sẽ phải dựa vào mô phòng Mockito của đoạn 1 và truyền .content(content) và xử lý theo mô phỏng đó và trả về đúng kết quả theo mô phỏng(Nên userDto1 phải reponse đúng value dự định mong muốn)


}