package com.marbl.spring_security_aop_auth.entity.user;

import com.marbl.spring_security_aop_auth.entity.provider.UserAuthProvider;
import com.marbl.spring_security_aop_auth.entity.role.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(schema = "auth", name = "users")
public class User implements Serializable, UserDetails {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email; //required

    @Column(unique = true)
    private String username; //optional used for classic login

    @Column(name = "password_hash")
    private String password; // Nullable if OAuth

    @Column(nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private int failedAttempts;

    private Timestamp lockedUntil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // === Relations ===
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            schema = "auth",
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAuthProvider> authProviders = new HashSet<>();

    // === Methods UserDetails ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return failedAttempts <= MAX_FAILED_ATTEMPTS; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}