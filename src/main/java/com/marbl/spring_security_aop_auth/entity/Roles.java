package com.marbl.spring_security_aop_auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(schema = "auth")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "name")
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<Users> users = new HashSet<>();

}
