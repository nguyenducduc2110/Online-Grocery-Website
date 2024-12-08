package com.springboot3.Web.of.spring.boot.auth.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Springboot3Exception extends RuntimeException {
    private ErrorDetails errorDetails;

    public Springboot3Exception(ErrorDetails errorDetails) {
        super(errorDetails.getMessage());
        this.errorDetails = errorDetails;
    }
}
