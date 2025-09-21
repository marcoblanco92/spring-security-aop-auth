package com.marbl.spring_security_aop_auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    private String username;
    @Column(unique = true)
    @Email(regexp = ".+@.+\\..+", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;
    @Column(name = "password_hash", nullable = false)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one special character"
    )
    private String password;
    @Column(nullable = false)
    private String salt;
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = Boolean.TRUE;
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private int failedAttempts;
    private Timestamp lockedUntil;
    private String oauthProvider;
    private String oauthId;
    private String oauthSecret;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            schema = "auth",
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

}
