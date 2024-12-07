package com.springboot3.Web.of.spring.boot.auth.dto.model.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private LocalDate dob;
    //trả về list DTO vừa để hạn chế data ít nhất trả về cho client vừa security
    private Set<RoleResponse> roles;

}
