package com.springboot3.Web.of.spring.boot.auth.dto.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//Dùng để gửi request chứa token cho việc test tính hợp lệ token(verify)
public class IntrospectRequest {
    @NotEmpty(message = "Token can not empty!")
    String token;
}
