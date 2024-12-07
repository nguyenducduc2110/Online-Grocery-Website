package com.springboot3.Web.of.spring.boot.auth.dto.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//Dùng để reponse kqua cho việc test tính hợp lệ token(verify)
//Có 1 fied nhưng dùng cả 1 class để trả về nhằm mục đích lâu dài vs ứng dụng lớn
//Ví dụ khi mở rộng thêm các trường như:time xác thực,...
public class IntrospectResponse {
    boolean valid;
}
