package com.polstat.roombooking.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String nama;
    private String kelas;
    private String nim;

    // Constructor, Getters and Setters

    public UserDTO(Long id, String email, String role, String nama, String kelas, String nim) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.nama = nama;
        this.kelas = kelas;
        this.nim = nim;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}

