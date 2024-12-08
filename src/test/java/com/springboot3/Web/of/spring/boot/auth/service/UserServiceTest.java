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



@Log4j
@AutoConfigureMockMvc
@SpringBootTestpublic class UserServiceTest {
        @Autowired
    private MockMvc mockMvc;
    private User user;
    private UserResponse userDto;

    private UserResponse userDto1;

        @MockBean    private UserService userService;
    @Autowired
    private UserRepository userRepository;
            //private  UserService userService = Mockito.mock(UserService.class);

            @BeforeEach
    void initData(){
                userDto = new UserResponse();
        userDto.setUsername("fasdfdfadadfa");
        userDto.setFirstName("Nguyen");
        userDto.setLastName("Duc");
        userDto.setPassword("123456");
                userDto1 = new UserResponse();
        userDto1.setId(1L);        userDto1.setUsername("fasdfdfadadfa");
        userDto1.setFirstName("Nguyen");
        userDto1.setLastName("Duc");
                user = User.builder()
                .id(1L)
                .username("fasdfdfadadfa")
                .firstName("Nguyen")
                .lastName("Duc")
                .build();
    }

            @Test
    @WithMockUser(username = "fasdfdfadadfa")    void getMyInfo_valid_success() throws Exception {
                                Mockito.when(userService.getMyInfo()).thenReturn(userDto1);
        var userDto = userService.getMyInfo();
                Assertions.assertThat(userDto.getUsername()).isEqualTo("fasdfdfadadfa");
        Assertions.assertThat(userDto.getId()).isEqualTo(1L);

    }

}
