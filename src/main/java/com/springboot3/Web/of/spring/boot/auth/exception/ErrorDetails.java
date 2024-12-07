package com.springboot3.Web.of.spring.boot.auth.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter//Chỉ dùng Getter vì setter là các enum là các object static chứa các value gán cứng(hard code) tương ứng vs các attribute của class
//Định nghĩa all các lỗi của user gặp phải mà app quy định
public enum ErrorDetails {
    //Enum này dạng static gọi đc = class
    //Và cấu trúc của 1 enum là 1 object static chứa các value gán cứng(hard code) tương ứng vs các attribute của class enum
    //Đây là enum notification lỗi chưa xác định ở server side(Exception chưa phân loại)
    UNCATEGORIED_EXCEPTION(9999, "Uncategoried erorr", HttpStatus.INTERNAL_SERVER_ERROR),
    //Đây là enum chung cho all các Validate, chỉ lấy code, httpStatus là chủ yếu, message lấy từ các annotaion validate
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    //Enum of client side
    USER_EXISTED(1002, "Username already exits!", HttpStatus.BAD_REQUEST),
    //ko tìm thấy user
    USER_NOT_EXISTED(1005, "Username not existed!", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1006, "Unauthenticate or You do not have access permissions!", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1007, "You do not have enough access permissions!", HttpStatus.FORBIDDEN),
    NOT_FOUND(1008, "Not found!", HttpStatus.NOT_FOUND),

    //Validate
//    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
//    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    ;
    ErrorDetails( int code, String message, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }
    private int code;//set cho code của ApiResonse
    //Mỗi Enum sẽ tương ứng 1 lỗi nên ko thể để List message đc.
    //Attibute này chắc chỉ dùng cho throw lỗi cứ controller ko dùng.
    private String message;//setcho messages của  ApiResonse
    private HttpStatus httpStatus;//set cho httpStatus của ResponseEntity ở handle exception
    //ApiResonse ko có trường này mà có fields T result vì còn để dùng reponse cho get data.->Nên khi reponse ko đc đưa message vào fileds T result
    //==>Dùng Enum kiểu này tốt vs hệ thống lớn vì tổng hợp đc all các lỗi mà user có thể gặp phải
}
