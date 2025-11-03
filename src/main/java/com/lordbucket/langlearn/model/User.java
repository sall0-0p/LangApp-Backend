package com.lordbucket.langlearn.model;

import com.lordbucket.langlearn.misc.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Basic user, contains usernameOrEmail, email and other authentication fields. Can be expanded later.
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    public User() {}

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Used to define if user have verified their email.
     */
    @Column
    private boolean enabled;

    /**
     * Used when verifying users, is sent as link on email, using the link will verify user email, and redirect them to the profile / login page.
     */
    @Column
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;

    @Enumerated(EnumType.STRING)
    private Role role = Role.User;

    // UserDetails methods

    /**
     * Returns the authorities granted to the user.
     * We'll just return a single role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}

