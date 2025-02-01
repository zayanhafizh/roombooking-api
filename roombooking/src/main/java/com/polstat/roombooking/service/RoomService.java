package com.polstat.roombooking.service;

import com.polstat.roombooking.entity.Room;
import com.polstat.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Method untuk membuat ruangan baru
    public Room createRoom(Room newRoom) {
        // Set default value untuk isAvailable jika tidak diatur
        if (newRoom.isAvailable() == false) {
            newRoom.setAvailable(true);
        }
        // Simpan ruangan baru ke dalam database
        return roomRepository.save(newRoom);
    }

    // Method untuk mengubah detail ruangan
    public Room updateRoom(Long id, Room updatedRoomDetails) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Perbarui nama ruangan jika ada perubahan
        if (updatedRoomDetails.getName() != null) {
            existingRoom.setName(updatedRoomDetails.getName());
        }

        // Perbarui status isAvailable jika ada perubahan
        existingRoom.setAvailable(updatedRoomDetails.isAvailable());

        return roomRepository.save(existingRoom);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
