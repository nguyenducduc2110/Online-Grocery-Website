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
    @Id
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    //private Set<User> users;Trong spring boot3 nó sẽ tự tạo Many cho Bảng N còn lại nhưng khi ko có methdo set cho attribute đó

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_code", referencedColumnName = "code"), inverseJoinColumns = @JoinColumn(name = "permission_code", referencedColumnName = "code")
    )
    private Set<Permission> permissions;
}

