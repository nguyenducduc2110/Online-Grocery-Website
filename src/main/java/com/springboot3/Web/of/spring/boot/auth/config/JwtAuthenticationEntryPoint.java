package com.springboot3.Web.of.spring.boot.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.ApiResponse;
import com.springboot3.Web.of.spring.boot.auth.exception.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

//Bean này dùng để custom reponse cho exception 401 Unauthorized khi JWT KO CÓ VALUE field (Scopse) or JWT KO ĐÚNG.
//===>VS all các exception nó đều ném vào bean này(exception configuration) để hanlde.Riêng 401 Unauthorized(ko có role) thì nó ném exception
//đó vào Spring Security(Nên phải config handle exception) ở Security.
//===>Chỉ cần lên jwt.io SỬA VỢI role cũng lỗi. VD: "scope": "ROLE_ADMIN ROLE_USER"->"ROLE_ADMIN" mặc dù method PreAuthorized:ROLE_ADMIN ->Vẫn trả về lỗi này
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        //Ko nên làm ntn vì tạo biến tạm gây tốn bộ nhớ. Còn cái dưới tham chiếu đến thẳng object enum public
        //ErrorDetails errorDetails = ErrorDetails.UNAUTHORIZED;
        response.setStatus(ErrorDetails.UNAUTHORIZED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse apiResponse = ApiResponse.builder()
                .code(ErrorDetails.UNAUTHORIZED.getCode())
                .messages(ErrorDetails.UNAUTHORIZED.getMessage())
                .build();
        //Convert object java sang string JSON
        //Bình thường config controller có annotaion @Controller nên nó tự chuyển object thành JSON khi return.CÒn config ko dùng annotaion @Controller nên phải tự custom convert JSON
        ObjectMapper objectMapper = new ObjectMapper();
        //Viết nội dung muốn trả về vào body
        response.getWriter().print(objectMapper.writeValueAsString(apiResponse));
        //method này dùng để  xả dữ liệu hiện tại trong buffer (bộ đệm) xuống client NGAY LẬP TỨC và xóa sạch buffer.
        response.flushBuffer();
    }
}
