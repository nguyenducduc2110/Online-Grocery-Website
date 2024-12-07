package com.springboot3.Web.of.spring.boot.auth.dto.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DO việc handle logic Auth nằm ở service controller chỉ việc gọi nên class này sinh ra để reponse object cho controller
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
}
