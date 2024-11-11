package com.polstat.roombooking.repository;

import com.polstat.roombooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Metode untuk mencari booking berdasarkan Room ID dan rentang waktu
    List<Booking> findByRoomIdAndStartTimeBetween(Long roomId, LocalDateTime start, LocalDateTime end);

    // Metode untuk mencari booking berdasarkan pengguna (email atau ID pengguna)
    List<Booking> findByBookedBy(String bookedBy);

    // Metode untuk mendapatkan semua booking untuk ruangan tertentu
    List<Booking> findByRoomId(Long roomId);

    // Metode untuk mencari booking yang berlangsung dalam rentang waktu tertentu
    List<Booking> findByStartTimeAfterAndEndTimeBefore(LocalDateTime start, LocalDateTime end);

    // Metode untuk mencari booking pada tanggal tertentu (misalnya, semua booking untuk hari ini)
    List<Booking> findByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
