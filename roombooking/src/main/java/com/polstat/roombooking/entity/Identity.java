package com.polstat.roombooking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "identities")
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false, unique = true)
    private String nim;

    @Column(nullable = false)
    private String kelas;

    @OneToOne(mappedBy = "identity")
    private User user;
}
