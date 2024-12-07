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

    // @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserReponse).toList();
    }

    //Lý do phân ra createUser và register để createUser tạo do quản trị viên, register tạo do client
    //UserDto có field id mà RegisterDto ko có vì UserDto để lấy id User thao tác và sửa info User.
    @Override
    public UserResponse createUser(UserCreationRequest userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        User user = userMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(PredefindedRole.USER_ROLE).orElseThrow(() -> {throw new Springboot3Exception(ErrorDetails.NOT_FOUND);}));
        user.setRoles(roles);
        try {
            userRepository.save(user);
            //ngoại lệ này ktra nếu data truyền vào vi phạm ko tuân theo fk, unique, null.
        } catch (DataIntegrityViolationException e) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        //Sau khi lưu User success lại map lại thành UserDto lý do là:
        //1.Bảo vệ dữ liệu nhạy cảm với những request yêu cầu sửa ngaysinh, name thì trả lại toàn bộ User:password,... làm gì,
        //chỉ trả lại Dto chứa các fields cần thiết.(Nguyên tắc "càng ít thông tin càng tốt")
        //2.Tối ưu dữ liệu trả về cho frontend. Nếu frontend chỉ cần trả User với 4 fields:name, address, username,dateOfBirth
        //mà UserEntity có cả fields:roles, Orders, createBy, createDate,... thì lúc query lấy User nó sẽ lấy hết.(Or dùng: JPQL để query những fields chỉ định)
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
                //Dùng exception:RuntimeException để nó vẫn ném ra đc exception lúc đang chạy(TT:SpringbootException)
                .orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return userMapper.toUserReponse(user);
    }


    //==>PostAuthorize Dùng khi muốn ktra object trả về sau khi handle. Nếu object trả về thỏa mãn thì ok..Còn vs PreAuthorize thì ko ktra object sau khi handle mà ktra data bên ngoài.
    //Khi ko thỏa mãn điều kiện annotaion này ném ra ngoại lệ cho handleAccessDeniedException handle và throw
//    hasRole:ADMIN, hasAuthority:ROLE_ADMIN
    //@PostAuthorize("returnObject.username  == authentication.name && (hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    //User vừa update sao có thể == user trong hệ thống khi update username.
    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    //tức là trong quá trình giao dịch mà hệ thống có bất kì 1 lỗi trong thời gian runtime thì lỗi
// ngay cả khi ko liên quan đến giao dịch thì @Transactional sẽ tự động rollback
    // bật giao dịch (@EnableTransactionManagement).
//    @Transactional(rollbackFor = {AccessDeniedException.class})
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {//Nếu chỉ muốn update 1 vài fiels thì phải tạo Dto chỉ có các trường muốn update
        User user = userRepository.findById(id).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        //Nếu mới update username thì phải ktra tên mới update đó đã có trong csdl chưa
        if (userRepository.existsByUsername(userUpdateRequest.getUsername())) {
            throw new Springboot3Exception(ErrorDetails.USER_EXISTED);
        }
        userMapper.updateUser(user, userUpdateRequest);//mapper này chỉ upadate những trường change của object hiện có. CHứ ko chuyển thành object có type mới như toUser
        //Do đoạn này Dto ko trả về id rỗng nên user save xuống id rỗng.Nhưng lại trùng userName có ở data base rồi nên lỗi
        //Vs createUser Dto cũng có id rỗng nhưng userName chưa có nên database cho phép save
        //Code trong phần generate source mỗi lần rebuild nó chỉ tạo ra code dựa annotaion, edit nó tự mất kể cả comment
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        List<Role> roles = roleRepository.findAllById(userUpdateRequest.getRoles());
        user.setRoles(new HashSet<>(roles));

        userRepository.save(user);//Hàm save sẽ lưu object và trả về object
        return userMapper.toUserReponse(user);
    }

    //Lấy info user hiện tại có in sys có trong JWT.
    @PreAuthorize("(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER'))")
    @Override
    public UserResponse getMyInfo() {
//        ===>SecurityContextHolder dùng để lưu trữ và cung cấp quyền truy cập vào đối tượng SecurityContext.
//        Còn SecurityContext lưu trữ thông tin xác thực của user hiện tại trong sys)
        var context = SecurityContextHolder.getContext().getAuthentication();
        String username = context.getName();
        User myUser = userRepository.findByUsername(username).orElseThrow(() -> new Springboot3Exception(ErrorDetails.USER_NOT_EXISTED));
        return userMapper.toUserReponse(myUser);
    }
}
