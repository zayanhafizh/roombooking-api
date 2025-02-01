package com.polstat.roombooking.controller;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.dto.BookingResponseDTO;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.service.BookingService;
import com.polstat.roombooking.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Get all bookings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponseDTO.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        List<BookingResponseDTO> bookingResponseDTOs = bookings.stream()
                .map(booking -> {
                    String nama = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNama() : "Unknown";
                    String nim = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNim() : "Unknown";
                    String kelas = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getKelas() : "Unknown";

                    return new BookingResponseDTO(
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getUser().getEmail(),
                            nama,
                            nim,
                            kelas,
                            booking.isAcc() // Tambahkan flag isAcc
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingResponseDTOs);
    }

    @Operation(summary = "Create a new booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid booking data", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingService.createBooking(bookingDTO, user);

        // Konversi Booking entity ke BookingResponseDTO
        BookingResponseDTO response = new BookingResponseDTO(
                booking.getId(),
                booking.getRoom().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getUser().getEmail(),  // Hanya kirim email, bukan seluruh user object
                booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNama() : "Unknown",
                booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNim() : "Unknown",
                booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getKelas() : "Unknown",
                booking.isAcc()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get bookings created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's bookings retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Booking> userBookings = bookingService.getUserBookings(user);
        List<BookingResponseDTO> bookingResponseDTOs = userBookings.stream()
                .map(booking -> {
                    String nama = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNama() : "Unknown";
                    String nim = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getNim() : "Unknown";
                    String kelas = booking.getUser().getIdentity() != null ? booking.getUser().getIdentity().getKelas() : "Unknown";

                    return new BookingResponseDTO(
                            booking.getId(),
                            booking.getRoom().getName(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getUser().getEmail(),
                            nama,
                            nim,
                            kelas,
                            booking.isAcc() // Tambahkan flag isAcc
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingResponseDTOs);
    }

    @Operation(summary = "Update an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid booking data", content = @Content)
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking updatedBooking = bookingService.updateBooking(id, bookingDTO, user);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Approve a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking approved successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    public ResponseEntity<String> approveBooking(@PathVariable Long id) {
        bookingService.approveBooking(id);
        return ResponseEntity.ok("Booking approved successfully");
    }

    @Operation(summary = "Delete a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN','USER')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get available rooms on a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available rooms retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room.class)))
    })
    @GetMapping("/available-rooms")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Room>> getAvailableRooms(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        List<Room> availableRooms = bookingService.getAvailableRooms(dateTime);
        return ResponseEntity.ok(availableRooms);
    }
}
