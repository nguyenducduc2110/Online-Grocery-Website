package com.springboot3.Web.of.spring.boot.auth.dto.mapper;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserUpdateRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

//@Component//Có cái này để biến object class này là bean
//@AllArgsConstructor
//public class UserMapper {
//
//    private ModelMapper mapper;//
//
//    public UserDto mapToDto(User user){
//        UserDto userDto = mapper.map(user, UserDto.class);
//        return userDto;
//    }
//
//    public User mapToEntity(UserDto userDto){
//        User user = mapper.map(userDto, User.class);
//        return user;
//    }
//}
//MapStruct tốt hơn vì ánh xạ được tạo sẵn ở compile-time,
//còn ModelMapper hoạt động ở runtime nên có thể chậm hơn.
@Mapper(componentModel = "spring")//biến interface này thành bean của spring container và auto generate các hàm mapping
public interface UserMapper {//Dùng interface để dảm bảo nguyên tắc IDP
    User toUser (UserCreationRequest userCreationRequest);
    //@Mapping(source = "firstName", target = "lastName")//map fields firstName of User sang lastName của UserDto
    //@Mapping(target = "lastName", ignore = true)// map cho field firstName = null của User sang firstName = null của UserDto
    UserResponse toUserReponse (User user);
    // để chỉ định đối tượng đích (target) sẽ được ánh xạ dữ liệu vào.
    //Khi mà update user thì phải find đúng user, Nên khi đã find đc thì dùng method này thì update UserDto do client nhập vào user đã find thấy và save
    //Tức là: updateUser là map vào user đã có để thay đổi data các trường update, còn toUser là convert thành type mới.

    //Bỏ qua mapping trường roles trong UserUpdateRequest vì roles đầu vào là mảng Id còn lúc request là mảng object Role
    // thì map lưu vào csdl gây lỗi. Kinh nghiệm là cứ class nào có fields là 1 list thì bỏ qua auto map mà tự map tay.
    @Mapping( target = "roles", ignore = true)
    void updateUser (@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}