package com.springboot3.Web.of.spring.boot.auth.dto.mapper;

import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserUpdateRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    UserResponse toUserReponse(User user);
    //Tức là: updateUser là map vào user đã có để thay đổi data các trường update, còn toUser là convert thành type mới.

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}