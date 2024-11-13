package com.polstat.roombooking.repository;

import com.polstat.roombooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Email(String email);

    List<Booking> findByUser_Identity_Nama(String nama);

    List<Booking> findByRoomIdAndStartTimeBetween(Long roomId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStartTimeAfterAndEndTimeBefore(LocalDateTime start, LocalDateTime end);

    List<Booking> findByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    // New Method: Find bookings that have not been approved
    List<Booking> findByIsAccFalse();
}
