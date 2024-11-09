package com.polstat.roombooking.controller;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.service.BookingService;
import com.polstat.roombooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        // Dapatkan email pengguna yang sedang login
        String userEmail = authentication.getName();

        // Cari pengguna berdasarkan email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Buat request booking
        Booking booking = bookingService.createBooking(bookingDTO, user);

        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        Booking updatedBooking = bookingService.updateBooking(id, bookingDTO); // Memastikan ada implementasi untuk memperbarui booking
        return ResponseEntity.ok(updatedBooking); // Mengembalikan entitas booking yang diperbarui
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id); // Memastikan ada implementasi untuk menghapus booking
        return ResponseEntity.noContent().build(); // Mengembalikan respons kosong dengan status 204
    }
}
