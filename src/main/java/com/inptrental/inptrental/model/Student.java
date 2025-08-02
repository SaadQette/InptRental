package com.inptrental.inptrental.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = "passwordHash") // don’t accidentally log the hash
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    // Academic email used as login; must be unique
    @Column(unique = true, nullable = false)
    private String inemail;

    private String phoneNumber;

    // Hashed password (never store raw password)
    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    // Email verification tracking
    private boolean emailVerified = false;

    @JsonIgnore
    private String verificationToken;

    @JsonIgnore
    private LocalDateTime verificationTokenExpiry;
}
