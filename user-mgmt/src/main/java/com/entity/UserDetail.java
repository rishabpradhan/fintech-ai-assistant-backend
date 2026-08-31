package com.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user_details")
public class UserDetail  extends AbstractEntity {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String hashedPassword;

    @Column(name = "otp_secrect")
    private String otpSecrect;

    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled;

    @Column(name = "login_attempts")
    private Integer loginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")

    )
    private Set<Roles> roles = new HashSet<>();

    public Boolean isLocked(){
        return lockedUntil!= null && lockedUntil.isAfter(Instant.now());
    }

}