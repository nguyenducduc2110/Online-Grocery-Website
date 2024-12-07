package com.springboot3.Web.of.spring.boot.auth.dto.model.request;

import com.springboot3.Web.of.spring.boot.auth.validator.DobContraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotNull
    //@NotEmpty(message = "Tên đầu không được bỏ trống")
    @Size(min = 1, message = "Tên đầu phải có độ dài tối thiểu là 1 ký tự")
    private String firstName;
    @NotEmpty(message = "Tên cuối không được bỏ trống")
    @Size(min = 1, message = "Tên cuối phải có độ dài tối thiểu là 1 ký tự")
    private String lastName;
    @NotEmpty(message = "Username không được bỏ trống")
    @Size(min = 5, message = "Mật khẩu phải có độ dài tối thiểu là 6 ký tự")
    private String username;
    @NotEmpty(message = "Mật khẩu không được bỏ trống")
    @Size(min = 6, message = "Mật khẩu phải có độ dài tối thiểu là 6 ký tự")
    private String password;

    @NotNull(message = "Ngày sinh không được bỏ trống")
    @DobContraint(min = 18, message = "Không đủ {min}+")
    private LocalDate dob;

    //@NotEmpty kiểm tra ko có giá trị truyền vào or postman request ko có field "roles"
    @NotEmpty(message = "Roles không được bỏ trống")
    @Size(min = 1)
    private Set<String> roles;

}
