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

    // Metode untuk membuat booking
    public Booking createBooking(BookingDTO bookingDTO, User user) {
        // Cari ruangan berdasarkan ID
        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validasi ketersediaan ruangan
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(bookingDTO.getRoomId(),
                        bookingDTO.getStartTime(),
                        bookingDTO.getEndTime());
        if (!overlappingBookings.isEmpty()) {
            throw new RoomNotAvailableException("Room is not available for the selected time period");
        }

        // Buat dan simpan booking baru
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setBookedBy(user.getEmail()); // Menggunakan email pengguna yang sedang login

        return bookingRepository.save(booking);
    }


    // Metode untuk memperbarui booking
    public Booking updateBooking(Long id, BookingDTO bookingDTO) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validasi ketersediaan ruangan untuk periode yang diperbarui
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(bookingDTO.getRoomId(),
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
}
