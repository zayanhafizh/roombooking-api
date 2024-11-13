package com.polstat.roombooking.controller;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.dto.BookingResponseDTO;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.service.BookingService;
import com.polstat.roombooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        // Konversi dari Booking ke BookingResponseDTO
        List<BookingResponseDTO> bookingResponseDTOs = bookings.stream()
                .map(booking -> {
                    String nama = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNama() : "Unknown";
                    String nim = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNim() : "Unknown";
                    String kelas = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getKelas() : "Unknown";

                    return new BookingResponseDTO(
                            booking.getRoom().getName(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getUser().getEmail(),
                            nama,
                            nim,
                            kelas
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingResponseDTOs);
    }

    // Endpoint to create a new booking
    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingService.createBooking(bookingDTO, user);
        return ResponseEntity.ok(booking);
    }

    // Endpoint to update an existing booking
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking updatedBooking = bookingService.updateBooking(id, bookingDTO, user);
        return ResponseEntity.ok(updatedBooking);
    }

    // Endpoint to approve a booking
    @PatchMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    public ResponseEntity<String> approveBooking(@PathVariable Long id) {
        bookingService.approveBooking(id);
        return ResponseEntity.ok("Booking approved successfully");
    }

    // Endpoint to delete a booking
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to get available rooms on a specific date
    @GetMapping("/available-rooms")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Room>> getAvailableRooms(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        List<Room> availableRooms = bookingService.getAvailableRooms(dateTime);
        return ResponseEntity.ok(availableRooms);
    }
}
