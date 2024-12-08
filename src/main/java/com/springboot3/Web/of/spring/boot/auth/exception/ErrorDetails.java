package com.springboot3.Web.of.spring.boot.auth.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getterpublic
enum ErrorDetails {
    UNCATEGORIED_EXCEPTION(9999, "Uncategoried erorr", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Username already exits!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Username not existed!", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1006, "Unauthenticate or You do not have access permissions!", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1007, "You do not have enough access permissions!", HttpStatus.FORBIDDEN),
    NOT_FOUND(1008, "Not found!", HttpStatus.NOT_FOUND),

    ;

    ErrorDetails(int code, String message, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    private int code;
    private String message;
    private HttpStatus httpStatus;
}
