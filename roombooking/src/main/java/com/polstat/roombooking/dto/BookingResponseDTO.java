package com.polstat.roombooking.dto;

import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long bookingId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userEmail;
    private String nama;
    private String nim;
    private String kelas;
    private boolean isAcc;

    // Constructor
    public BookingResponseDTO(Long bookingId,String roomName, LocalDateTime startTime, LocalDateTime endTime,
                              String userEmail, String nama, String nim, String kelas,boolean isAcc) {
        this.bookingId = bookingId;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userEmail = userEmail;
        this.nama = nama;
        this.nim = nim;
        this.kelas = kelas;
        this.isAcc = isAcc;
    }

    // Getters and Setters
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public boolean isAcc() {
        return isAcc;
    }

    public void setAcc(boolean isAcc) {
        this.isAcc = isAcc;
    }

    // Getters and Setters
    public Long getId() {
        return bookingId;
    }

    public void setId(Long id) {
        this.bookingId = bookingId;
    }
}
