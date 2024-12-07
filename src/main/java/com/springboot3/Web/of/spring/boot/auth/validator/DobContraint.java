package com.springboot3.Web.of.spring.boot.auth.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = {DobValidator.class})//Là class chịu trách nhiệm logic validate of this annotation
@Target({FIELD})//chỉ các attribute mới đc dùng annotation này
@Retention(RUNTIME)//1//chỉ định annotation hoạt động lúc nào.Và đây là khi runtime sẽ gọi annotation và annotation này sẽ gọi method trong class handle logic validate
public @interface DobContraint {
    //==>Đây conversion attribute của annotation
    //default cung cấp giá trị mặc định cho attribute. Và value default có thể bị ghì đè
    //
    String message() default "Invalid data of birth";

    //ko để default init value default thì khi dùng annotation này bắt buộc dùng attribute min
    int min();

    Class<?>[] groups() default { };// Nhóm cho validation (ít dùng)

    Class<? extends Payload>[] payload() default { };// Metadata (ít dùng)
}
