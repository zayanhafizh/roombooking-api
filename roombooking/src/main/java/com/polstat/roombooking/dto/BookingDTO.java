package com.polstat.roombooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookedBy;
}
