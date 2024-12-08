package com.springboot3.Web.of.spring.boot.auth.dto.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

//Class này response vs all từ:nhận lỗi và trả ra, reponse object,...

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T, M> {
    @Builder.Default
    private int code = 1000;
    private M messages;
    private T result;
}
