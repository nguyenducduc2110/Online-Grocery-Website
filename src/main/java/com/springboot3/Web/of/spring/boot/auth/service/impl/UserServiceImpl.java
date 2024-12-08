package com.springboot3.Web.of.spring.boot.auth.service.impl;

import com.springboot3.Web.of.spring.boot.auth.contant.PredefindedRole;
import com.springboot3.Web.of.spring.boot.auth.dto.mapper.UserMapper;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserCreationRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.request.UserUpdateRequest;
import com.springboot3.Web.of.spring.boot.auth.dto.model.response.UserResponse;
import com.springboot3.Web.of.spring.boot.auth.entity.Role;
import com.springboot3.Web.of.spring.boot.auth.entity.User;
import com.springboot3.Web.of.spring.boot.auth.exception.ErrorDetails;
import com.springboot3.Web.of.spring.boot.auth.exception.Springboot3Exception;
import com.springboot3.Web.of.spring.boot.auth.repository.RoleRepository;
import com.springboot3.Web.of.spring.boot.auth.repository.UserRepository;
import com.springboot3.Web.of.spring.boot.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private RoleRepository roleRepository;

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserReponse).toList();
    }

    @Override
    public UserResponse createUser(UserCreationRequest userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        User user = userMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(PredefindedRole.USER_ROLE).orElseThrow(() -> {
            throw new Springboot3Exception(ErrorDetails.NOT_FOUND);
        }));
        user.setRoles(roles);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        //Nên việc map lại Dto thì nó chỉ map những fields mà Dto có.

        return userMapper.toUserReponse(user);
    }

    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return userMapper.toUserReponse(user);
    }


    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        if (userRepository.existsByUsername(userUpdateRequest.getUsername())) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        userMapper.updateUser(user, userUpdateRequest);
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        List<Role> roles = roleRepository.findAllById(userUpdateRequest.getRoles());
        user.setRoles(new HashSet<>(roles));

        userRepository.save(user);
        return userMapper.toUserReponse(user);
    }

    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext().getAuthentication();
        String username = context.getName();
        User myUser = userRepository.findByUsername(username).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return userMapper.toUserReponse(myUser);
    }
}
