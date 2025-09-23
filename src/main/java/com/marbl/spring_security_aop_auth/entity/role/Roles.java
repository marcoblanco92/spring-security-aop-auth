package com.marbl.spring_security_aop_auth.entity.role;

import com.marbl.spring_security_aop_auth.entity.user.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(schema = "auth",name = "roles")
public class Roles implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, name = "name")
    private RolesEnum roleName;

}
