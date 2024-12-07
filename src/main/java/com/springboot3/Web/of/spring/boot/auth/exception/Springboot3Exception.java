package com.springboot3.Web.of.spring.boot.auth.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//===>Nói chung class này dùng để nhận exception lúc runtime và ErrorDetails là object enum throw ra lỗi lúc runtime
//Class này exception của app và nó nhận thông báo riêng của từng exception và dùng để throw ra
//Kế thừa RuntimeException để khi exception lúc chạy ném ra thì class này extend RuntimeException thì nhận đc exception
public class Springboot3Exception extends RuntimeException {
    //Và class có attribute enum này để lấy ra đc errorDetails lúc nó throw lỗi
    //Và class làm param cho  method handler lỗi do client mà app quy định
    private ErrorDetails errorDetails;
    public Springboot3Exception(ErrorDetails errorDetails) {
        super(errorDetails.getMessage());
        this.errorDetails = errorDetails;
    }
}
