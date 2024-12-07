package com.springboot3.Web.of.spring.boot.auth.entity;

import com.springboot3.Web.of.spring.boot.utils.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
@Entity
public class Permission extends BaseEntity {
    //Với nhưng trường code của role, permission chỉ là duy nhất nên cho làm id luôn để tránh nhiều cột gây giảm perfomance
    @Id
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
