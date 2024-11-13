package com.polstat.roombooking.service;

import com.polstat.roombooking.dto.BookingDTO;
import com.polstat.roombooking.entity.Booking;
import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.entity.User;
import com.polstat.roombooking.exception.RoomNotAvailableException;
import com.polstat.roombooking.repository.BookingRepository;
import com.polstat.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors; // Import Collectors

@Service
public class BookingService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    //Method to get all booking
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Method to create a new booking
    public Booking createBooking(BookingDTO bookingDTO, User user) {
        LocalDateTime now = LocalDateTime.now();

        //Validasi start sama end bookingnya ada
        if (bookingDTO.getStartTime() == null ||
                bookingDTO.getEndTime() == null ||
            bookingDTO.getRoomName() == null) {
            throw new IllegalArgumentException("Please fulfill the request data");
        }

        // Validasi: Pastikan startTime dan endTime tidak berada di masa lalu
        if (bookingDTO.getStartTime().isBefore(now) || bookingDTO.getEndTime().isBefore(now)) {
            throw new IllegalArgumentException("Booking cannot be made for past dates");
        }

        // Validasi: Pastikan endTime tidak lebih awal dari startTime
        if (bookingDTO.getEndTime().isBefore(bookingDTO.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be earlier than start time");
        }

        // Validasi: Pastikan durasi booking tidak lebih dari 1 hari
        if (ChronoUnit.DAYS.between(bookingDTO.getStartTime(), bookingDTO.getEndTime()) >= 1) {
            throw new IllegalArgumentException("Booking duration cannot exceed one day");
        }

        Room room = roomRepository.findByName(bookingDTO.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Check if the room is available
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room is currently not available for booking");
        }

        // Filter overlapping bookings to only include those that are approved
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(room.getId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())
                .stream()
                .filter(Booking::isAcc) // Only consider approved bookings for conflict check
                .collect(Collectors.toList());

        if (!overlappingBookings.isEmpty()) {
            throw new RoomNotAvailableException("Room is not available for the selected time period");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setUser(user);
        booking.setAcc(false); // Default to not approved
        booking.setCreatedAt(now); // Set the createdAt timestamp

        return bookingRepository.save(booking);
    }

    // Method to update an existing booking
    public Booking updateBooking(Long id, BookingDTO bookingDTO, User user) {
        LocalDateTime now = LocalDateTime.now();

        // Validasi: Pastikan startTime dan endTime tidak berada di masa lalu
        if (bookingDTO.getStartTime().isBefore(now) || bookingDTO.getEndTime().isBefore(now)) {
            throw new IllegalArgumentException("Booking cannot be made for past dates");
        }

        // Validasi: Pastikan endTime tidak lebih awal dari startTime
        if (bookingDTO.getEndTime().isBefore(bookingDTO.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be earlier than start time");
        }

        // Validasi: Pastikan durasi booking tidak lebih dari 1 hari
        if (ChronoUnit.DAYS.between(bookingDTO.getStartTime(), bookingDTO.getEndTime()) >= 1) {
            throw new IllegalArgumentException("Booking duration cannot exceed one day");
        }

        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Room room = roomRepository.findByName(bookingDTO.getRoomName())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Check if the room is available
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room is currently not available for booking");
        }

        // Filter overlapping bookings to only include those that are approved
        List<Booking> overlappingBookings = bookingRepository
                .findByRoomIdAndStartTimeBetween(room.getId(), bookingDTO.getStartTime(), bookingDTO.getEndTime())
                .stream()
                .filter(Booking::isAcc) // Only consider approved bookings for conflict check
                .collect(Collectors.toList());

        // Ensure we are not counting the existing booking as a conflict
        if (!overlappingBookings.isEmpty() && !overlappingBookings.contains(existingBooking)) {
            throw new RoomNotAvailableException("Room is not available for the selected time period");
        }

        existingBooking.setRoom(room);
        existingBooking.setStartTime(bookingDTO.getStartTime());
        existingBooking.setEndTime(bookingDTO.getEndTime());
        existingBooking.setUser(user); // Update the user who is making the booking

        return bookingRepository.save(existingBooking);
    }

    // Method to approve a booking
    public void approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        booking.setAcc(true); // Use setAcc to set booking as approved
        bookingRepository.save(booking);
    }

    // Method to delete a booking
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        bookingRepository.delete(booking);
    }

    // Method to get available rooms on a specific date
    public List<Room> getAvailableRooms(LocalDateTime date) {
        List<Booking> bookingsOnDate = bookingRepository.findByStartTimeBetween(
                date.toLocalDate().atStartOfDay(),
                date.toLocalDate().atTime(23, 59)
        );

        List<Long> bookedRoomIds = bookingsOnDate.stream()
                .filter(Booking::isAcc) // Only consider approved bookings
                .map(booking -> booking.getRoom().getId())
                .collect(Collectors.toList());

        return roomRepository.findAll().stream()
                .filter(room -> room.isAvailable()) // Check if the room is available
                .filter(room -> !bookedRoomIds.contains(room.getId())) // Check if the room is not booked
                .collect(Collectors.toList());
    }

    //Method to get user booking
    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser_Email(user.getEmail());
    }
}
