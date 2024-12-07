package com.springboot3.Web.of.spring.boot.auth.entity;

import com.springboot3.Web.of.spring.boot.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class Role extends BaseEntity {
    //Với nhưng trường code của role, permission chỉ là duy nhất nên cho làm id luôn để tránh nhiều cột gây giảm perfomance
    @Id
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    //Ko declare fields này nhưng vẫn get đc do nó tự cấu hình.
    //private Set<User> users;Trong spring boot3 nó sẽ tự tạo Many cho Bảng N còn lại nhưng khi ko có methdo set cho attribute đó

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)//N-N nên dùng merge
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_code", referencedColumnName = "code"),  // Chỉnh lại "permissoin_id" nếu đó là lỗi chính tả
            inverseJoinColumns = @JoinColumn(name = "permission_code", referencedColumnName = "code")
    )
    private Set<Permission> permissions;
}

