package com.springboot3.Web.of.spring.boot.auth.entity;

import com.springboot3.Web.of.spring.boot.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Set ntn thì lúc save user thì JPA nó sẽ tự tạo id
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    //fields nào ko truyền vào nên để null
    private LocalDate dob;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_code", referencedColumnName = "code")
    )
    //Đại diện cho việc 1 user có nhiều role. Tức là cùng 1 id trong table users_roles có nhều row data id_role
    private Set<Role> roles;

}