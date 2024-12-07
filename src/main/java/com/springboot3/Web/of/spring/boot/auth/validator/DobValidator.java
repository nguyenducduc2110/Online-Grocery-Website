package com.springboot3.Web.of.spring.boot.auth.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

//Là người handle logic validate cho dữ liệu truyền vào 1 biến bằng cách
//lấy điều kiện là value truyền vào cho annotation custom so sánh vs value truyền vào biến.
public class DobValidator implements ConstraintValidator<DobContraint, LocalDate> {//<annotation chứa điều kiện, datatype of value biến muốn validate>
    int min;

    //2//Lúc runtime thì method này đc annotation gọi đầu để lấy value truyền vào attribute của annotation custom.
    @Override
    public void initialize(DobContraint constraintAnnotation) {
//3//Đây là cung cấp value of attribute of annotation @DobContraint cho biến
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    //4//annotation custom sẽ lấy value của biến và truyền vào param LocalDate value để so sánh vs value của attribute annotation custom
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if(Objects.isNull(value)) return true;
        //Đoạn code này so sánh năm hiện tại vs năm sinh và trả về số tuổi là số năm chệnh lệch
        long years = ChronoUnit.YEARS.between(value, LocalDate.now());
        //5//Nếu >= 18 thì ok ngược lại false thì cho lấy message để throw exception
        return years>=min;
    }
}
