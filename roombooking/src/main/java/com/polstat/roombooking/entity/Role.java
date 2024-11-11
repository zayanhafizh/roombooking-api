package com.polstat.roombooking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType name;

    // Tambahkan metode helper
    public String getRoleNameAsString() {
        return this.name.name();
    }
}