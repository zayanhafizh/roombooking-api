package com.polstat.roombooking.controller;

import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Create a new room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Room> createRoom(@RequestBody Room newRoom) {
        Room createdRoom = roomService.createRoom(newRoom);
        return ResponseEntity.ok(createdRoom);
    }

    @Operation(summary = "Update room details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room.class))),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room updatedRoomDetails) {
        Room updatedRoom = roomService.updateRoom(id, updatedRoomDetails);
        return ResponseEntity.ok(updatedRoom);
    }
}
