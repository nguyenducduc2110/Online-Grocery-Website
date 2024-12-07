package com.springboot3.Web.of.spring.boot.auth.dto.model.request;

import com.springboot3.Web.of.spring.boot.auth.validator.DobContraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {//Phân ra DTO: Creation, update,,... vì lúc tạo sẽ có các trường ko như update. Mà đôi khi admin có thể update role cho User nên phải phân ra
    private Long id;
    @NotEmpty(message = "Tên đầu không được bỏ trống")
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
    @DobContraint(message = "Không đủ 18+", min = 18)
    private LocalDate dob;

}
