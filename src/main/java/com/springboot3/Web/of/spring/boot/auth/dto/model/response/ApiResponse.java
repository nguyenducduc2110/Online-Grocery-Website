package com.springboot3.Web.of.spring.boot.auth.dto.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

//Class này sẽ thay ResponseEntity reponse cho client vì nó có reponse thống nhất.
//Có thể tạo riêng để định dạng như class ErorrDetails.Nhưng chỉ định dạng cho Erorr thôi.Còn đây là đang muốn định dạng form cho all response
//VD:Với ErorrDetails
//        KHi Username đã tồn tại
//        {
//                "message": "Username already exits!",
//                "timeStamp": "2024-09-18T04:41:39.324+00:00",
//                "details": "uri=/springboot3/signup"
//        }
//      KHI Lấy user.===>Cho thấy ko cùng định dạng nên fron-end phải tạo nhiều fuction handler riêng cho từng response
//{
//        "createdDate": "2024-09-01",
//        "modifiedDate": "2024-09-17",
//        "createdBy": "admin",
//        "modifiedBy": "admin",
//        "id": 1,
//        "firstName": "Nguyễnn",
//        "lastName": "Linh",
//        "username": "Vfdasf",
//        "password": "$2a$10$MD43WchfT4rWnra4p/eCZeRzgbdUpRRHSc9iSCvaO8bpw.Nyuqa4G",
//        "dob": "1990-01-01",
//        "roles": [
//        {
//        "createdDate": "2024-09-01",
//        "modifiedDate": "2024-09-01",
//        "createdBy": "admin",
//        "modifiedBy": "admin",
//        "id": 1,
//        "name": "Administrator",
//        "code": "ADMIN"
//        }
//        ]
//        }
//Class này response vs all từ:nhận lỗi và trả ra, reponse object,...

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder//giúp tạo object rõ ràng và chỉ định ngắn hơn việc set
@FieldDefaults(level = AccessLevel.PRIVATE)//set auto các fields có scopse private trừ khi tự set handmade
public class ApiResponse<T, M> {
    //Mặc dù có HttpStatus trên header trên code đây giúp frontend xác định status dễ hơn
    //code default là 1000. Nếu get data mà code response là 1000 là success
    @Builder.Default//đảm bảo rằng nếu một trường không được set giá trị khi sử dụng builder
    private int code = 1000;
    //Vs validateException có thể là mảng message.Còn vs còn lại là 1 message
    //Tránh việc lúc nào cũng phải tạo list các message gấn tốn bộ nhớ
    private M messages;
    private T result;
    //===>Nếu là các ứng dụng bank thì nên các thêm trường Date.VÌ mỗi lần transaction thì nó sẽ trả về thời gian ngày giờ giao dịch
}
