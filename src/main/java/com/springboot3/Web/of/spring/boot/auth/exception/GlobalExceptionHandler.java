package com.springboot3.Web.of.spring.boot.auth.exception;

import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {//    ===>Method trên ko chung định dạng nên cấu hình method khác


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?, List<String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();


        ErrorDetails errorDetails = ErrorDetails.INVALID_KEY;
        ApiResponse<?, List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorDetails.getCode());
        apiResponse.setMessages(errorMessages);
        return ResponseEntity.status(errorDetails.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(Springboot3Exception.class)
    public ResponseEntity<ApiResponse<?, String>> handleAppException(Springboot3Exception ex) {
        ApiResponse<?, String> apiResponse = new ApiResponse<>();
        apiResponse.setMessages(ex.getMessage());
        apiResponse.setCode(ex.getErrorDetails().getCode());
        return ResponseEntity.status(ex.getErrorDetails().getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void, String>> handleBlogAPIException(Exception ex) {
        ApiResponse<Void, String> apiResponse = new ApiResponse<>();
        apiResponse.setMessages(ex.getMessage());
        apiResponse.setCode(ErrorDetails.UNCATEGORIED_EXCEPTION.getCode());
        return ResponseEntity.status(ErrorDetails.UNCATEGORIED_EXCEPTION.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void, String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(ErrorDetails.FORBIDDEN.getHttpStatus())
                .body(ApiResponse.<Void, String>builder()
                        .messages(ErrorDetails.FORBIDDEN.getMessage() + ex.getMessage()).code(ErrorDetails.FORBIDDEN.getCode())
                        .build());
    }
}
