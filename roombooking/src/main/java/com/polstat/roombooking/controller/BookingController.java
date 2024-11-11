package com.polstat.roombooking.controller;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.repository.RoomRepository;
import com.polstat.roombooking.service.BookingService;
import com.polstat.roombooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Booking booking = bookingService.createBooking(bookingDTO, user);
        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        System.out.println("Access granted to updateBooking with role ADMIN or SUPERADMIN");
        Booking updatedBooking = bookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Booking>> getBookingsByUser(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Booking> bookings = bookingService.getBookingsByUser(userEmail);
        return ResponseEntity.ok(bookings);
    }

    // Ubah endpoint untuk menerima roomName sebagai parameter
    @GetMapping("/room/{roomName}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Booking>> getBookingsByRoom(@PathVariable String roomName) {
        Room room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        List<Booking> bookings = bookingService.getBookingsByRoom(room.getId()); // gunakan ID ruangan
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Booking>> getBookingsWithinPeriod(
            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<Booking> bookings = bookingService.getBookingsWithinPeriod(start, end);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Booking>> getBookingsForDate(
            @RequestParam LocalDateTime date) {
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
        List<Booking> bookings = bookingService.getBookingsForDate(startOfDay, endOfDay);
        return ResponseEntity.ok(bookings);
    }
}
