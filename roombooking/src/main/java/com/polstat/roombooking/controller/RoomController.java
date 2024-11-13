package com.polstat.roombooking.controller;

import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Endpoint untuk membuat ruangan baru
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Room> createRoom(@RequestBody Room newRoom) {
        Room createdRoom = roomService.createRoom(newRoom);
        return ResponseEntity.ok(createdRoom);
    }

    // Endpoint untuk mengubah detail ruangan
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room updatedRoomDetails) {
        Room updatedRoom = roomService.updateRoom(id, updatedRoomDetails);
        return ResponseEntity.ok(updatedRoom);
    }
}
