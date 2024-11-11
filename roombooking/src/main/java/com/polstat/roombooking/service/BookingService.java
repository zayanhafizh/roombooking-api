package com.polstat.roombooking.service;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.exception.RoomNotAvailableException;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.repository.BookingRepository;
import com.polstat.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(BookingDTO bookingDTO, User user) {
        // Cari ruangan berdasarkan nama
        Room room = roomRepository.findByName(bookingDTO.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validasi ketersediaan ruangan
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(room.getId(),
                        bookingDTO.getStartTime(),
                        bookingDTO.getEndTime());
        if (!overlappingBookings.isEmpty()) {
            throw new RoomNotAvailableException("Room is not available for the selected time period");
        }

        // Buat dan simpan booking baru
        Booking booking = new Booking();
        booking.setRoom(room); // Pastikan room sudah di-set
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setBookedBy(user.getEmail());

        return bookingRepository.save(booking);
    }


    // Metode untuk memperbarui booking
    public Booking updateBooking(Long id, BookingDTO bookingDTO) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Cari ruangan berdasarkan nama, bukan ID
        Room room = roomRepository.findByName(bookingDTO.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validasi ketersediaan ruangan untuk periode yang diperbarui
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(room.getId(), // Ambil ID dari room yang ditemukan
                        bookingDTO.getStartTime(),
                        bookingDTO.getEndTime());
        if (!overlappingBookings.isEmpty() && !overlappingBookings.contains(existingBooking)) {
            throw new RoomNotAvailableException("Room is not available for the selected time period");
        }

        existingBooking.setRoom(room);
        existingBooking.setStartTime(bookingDTO.getStartTime());
        existingBooking.setEndTime(bookingDTO.getEndTime());
        existingBooking.setBookedBy(bookingDTO.getBookedBy());

        return bookingRepository.save(existingBooking);
    }

    // Metode untuk menghapus booking
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        bookingRepository.delete(booking);
    }

    // Metode untuk melihat semua booking
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Metode untuk melihat semua booking berdasarkan pengguna
    public List<Booking> getBookingsByUser(String bookedBy) {
        return bookingRepository.findByBookedBy(bookedBy);
    }

    // Metode untuk mendapatkan semua booking berdasarkan ruangan
    public List<Booking> getBookingsByRoom(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    // Metode untuk mendapatkan booking pada periode waktu tertentu
    public List<Booking> getBookingsWithinPeriod(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByStartTimeAfterAndEndTimeBefore(start, end);
    }

    // Metode untuk mendapatkan booking pada tanggal tertentu
    public List<Booking> getBookingsForDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);
    }
}
