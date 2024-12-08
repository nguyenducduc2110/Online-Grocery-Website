package com.springboot3.Web.of.spring.boot.auth.dto.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotEmpty(message = "Name không được bỏ trống")
    @Size(min = 25, message = "Name phải có độ dài tối thiểu là 6 ký tự")
    String name;
    @NotEmpty(message = "Code không được bỏ trống")
    @Size(min = 25, message = "Code phải có độ dài tối thiểu là 6 ký tự")
    String code;
    @NotEmpty(message = "Permissions không được bỏ trống")
    @Size(min = 25, message = "Permissions phải có độ dài tối thiểu là 6 ký tự")
    Set<String> permissions;
}
