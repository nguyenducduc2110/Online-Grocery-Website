package com.springboot3.Web.of.spring.boot.auth.dto.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "FirstName should not be empty!")
    @Size(min = 1, message = "FirstName should contain least 1 characters!")
    private String firstName;
    @NotEmpty(message = "LastName should not be empty!")
    @Size(min = 1, message = "LastName should contain least 1 characters!")
    private String lastName;
    @NotEmpty(message = "Username can not be empty!")
    @Size(min = 6, message = "Username must contain least 6 characters!")
    private String username;
    @NotEmpty(message = "Password can not be empty!")
    @Size(min = 6, message = "Password must contain least 6 characters!")
    private String password;
}
