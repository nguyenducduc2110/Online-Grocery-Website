package com.springboot3.Web.of.spring.boot.auth.dto.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty(message = "User can not be empty!")
    @Size(min = 5, max = 15, message = "Username containt least 6-15 charaters")
    private String username;
    @NotEmpty(message = "Password can not be empty!")
    @Size(min = 6, max = 15,message = "Password containt least 6-15 charaters")
    private String password;
}
