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
//===>VS all các exception nó đều ném vào bean này(exception configuration) để hanlde.Riêng 401 Unauthorized(ko có role) thì nó ném exception
//đó vào Spring Security(Nên phải config handle exception) ở Security.
//Do handleMethodArgumentNotValid ko trả về định dạng ApiResponse nên ko extend nữa
public class GlobalExceptionHandler {//extends ResponseEntityExceptionHandler {
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldError = ((FieldError) error).getField();
//            errors.put(fieldError, error.getDefaultMessage());
//        });
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
//    ===>Method trên ko chung định dạng nên cấu hình method khác


    //Lý do ko dùng ApiResponse để trả về lỗi vì nó ko có attribute status, còn ResponseEntity đầy đủ info của Response
    //KIểu này hay hơn devteria vì Validation không nằm trong enum mà tập trung vào lỗi hệ thống tránh phụ thuộc vào enum
    //->Dẫn đến hệ thống dễ mở rộng và ko bị trùng lặp enum(devteria chỉ dễ quản lý lỗi vì tập chung lỗi vào 1 class).
    //Chỉ định class này nhận các exception của validate vào param MethodArgumentNotValidException và hander`
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<?,List<String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    List<String> errorMessages = ex.getBindingResult()//Lấy ra all các lỗi trói buộc với info annotation validate
            //Lấy all các fields lỗi
            .getFieldErrors()
            //Chuyển List<FieldError> thành luồng data kiểu List List<FieldError>
            .stream()
            //Method map nhận logic handle cho từng element của List
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            //Method này thu thập các value từ Stream và chuyển nó thành 1 list
            .toList();


    //Đây là enum chung cho all các Validate, chỉ lấy code, httpStatus là chủ yếu, message lấy từ các annotaion validate
    ErrorDetails errorDetails = ErrorDetails.INVALID_KEY;
    //? là kiểu of body, List<String> kiểu of message(Có thể là 1 message or 1 list message)
    ApiResponse<?,List<String>> apiResponse = new ApiResponse<>();
    apiResponse.setCode(errorDetails.getCode());
    apiResponse.setMessages(errorMessages);
    //Dùng apiResponse cho vào body để chung định dạng trả về cho frontend.
    return ResponseEntity.status(errorDetails.getHttpStatus()).body(apiResponse);
}

    //Nếu class này có object exception đc ném ra thì annotation này bắt(catch) object exception vào param cùng kiểu chứa đc objectException bị ném
    @ExceptionHandler(Springboot3Exception.class)
    public ResponseEntity<ApiResponse<?, String>> handleAppException(Springboot3Exception ex) {
//        List<String> messages = new ArrayList<>();
//        messages.add(ex.getMessage());
        ApiResponse<?, String> apiResponse = new ApiResponse<>();
        apiResponse.setMessages(ex.getMessage());//NHững cái nào throw dev tự ném thì dùng param nhận exception để lấy message.
        apiResponse.setCode(ex.getErrorDetails().getCode());
        //setHttpStatus cho ResponseEntity và body là cấu trúcApiResponse chung
        //Do lúc throw ra exception thì enum object static có HttpStatus gán cứng sẵn rồi
        return ResponseEntity.status(ex.getErrorDetails().getHttpStatus()).body(apiResponse);
    }

    //Các ngoại lệ ko đc phân loại sẽ vào logic này.(Tức là vd annotaion @PreRequest ném expection AccessDenied)
    // mà chưa config handle @ExceptionHandler(value = AccessDeniedException.class) cho type exception này
    //==>Thì nó ném chung vào logic để handle.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void, String>> handleBlogAPIException(Exception ex) {
//        List<String> messages = new ArrayList<>();
//        messages.add(ErrorDetails.UNCATEGORIED_EXCEPTION.getMessage());
        ApiResponse<Void, String> apiResponse = new ApiResponse<>();
        //Do method hanle all các error còn lại chưa có nên chưa đc quy định chưa biết lỗi ở đâu ko ai throw ra nên cứ gán cứng quy định các exception còn lại
        apiResponse.setMessages(ex.getMessage());
        apiResponse.setCode(ErrorDetails.UNCATEGORIED_EXCEPTION.getCode());
        return ResponseEntity.status(ErrorDetails.UNCATEGORIED_EXCEPTION.getHttpStatus()).body(apiResponse);
    }
    //Method này handle khi user gửi request mà request đó ko có quyền access sẽ trả ra exception.(Do annotaion Pre(Post)Request, config ném ra exception
    //===>Ko cần config handleAccessDenied ở SecurityConfig
    @ExceptionHandler(value = AccessDeniedException.class)
    //SecurityConfig sẽ ném ra objectException của class AccessDeniedException và method này sẽ auto nhận các exception AccessDenied để throw
    //Exception này chỉ đc ném ra khi user đã login vào sys
    public  ResponseEntity<ApiResponse<Void, String>> handleAccessDeniedException(AccessDeniedException  ex) {
        return ResponseEntity.status(ErrorDetails.FORBIDDEN.getHttpStatus())
                .body(ApiResponse.<Void, String>builder()
                        .messages(ErrorDetails.FORBIDDEN.getMessage()+ex.getMessage())//NHững cái nào throw dev tự ném thì dùng param nhận exception để lấy message.
                        .code(ErrorDetails.FORBIDDEN.getCode())
                        .build());                                  //Còn những exception ở method này là annotation bắt và tự ném(Nên phải custom lại trừ exception chưa phân loại)
    }
}
